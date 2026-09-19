package com.brajesh.androidautovideo;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String DEFAULT_VIDEO_URL = "https://m.youtube.com/";

    private EditText videoUrlInput;
    private LinearLayout root;
    private WebView webView;
    private View customVideoView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildContentView());
        loadUrl(DEFAULT_VIDEO_URL);
    }

    private View buildContentView() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(14), dp(14), dp(14));
        root.setBackgroundColor(Color.rgb(18, 18, 20));

        TextView title = text("Android Auto Video", 28, true);
        root.addView(title);

        TextView subtitle = text(
                "Enter a YouTube URL and play it inside this app. Use only while parked or on a passenger-safe display.",
                16,
                false
        );
        subtitle.setPadding(0, dp(8), 0, dp(12));
        root.addView(subtitle);

        TextView status = text(carModeText(), 15, false);
        status.setTextColor(Color.rgb(120, 210, 160));
        status.setPadding(0, 0, 0, dp(12));
        root.addView(status);

        videoUrlInput = new EditText(this);
        videoUrlInput.setSingleLine(true);
        videoUrlInput.setText(DEFAULT_VIDEO_URL);
        videoUrlInput.setHint("Paste YouTube URL or enter search text");
        videoUrlInput.setTextColor(Color.WHITE);
        videoUrlInput.setHintTextColor(Color.rgb(160, 160, 165));
        videoUrlInput.setBackgroundColor(Color.rgb(35, 35, 40));
        videoUrlInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        root.addView(videoUrlInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Button openButton = new Button(this);
        openButton.setText("Load in App Browser");
        openButton.setAllCaps(false);
        openButton.setOnClickListener(v -> openVideo());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dp(10), 0, dp(10));
        root.addView(openButton, buttonParams);

        webView = new WebView(this);
        configureWebView(webView);
        root.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        return root;
    }

    private void openVideo() {
        String url = videoUrlInput.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a YouTube URL or search text.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadUrl(normalizeUrl(url));
    }

    private void configureWebView(WebView view) {
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setUserAgentString(settings.getUserAgentString() + " AndroidAutoVideo");

        view.setBackgroundColor(Color.BLACK);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String nextUrl = request.getUrl().toString();
                videoUrlInput.setText(nextUrl);
                return false;
            }
        });
        view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customVideoView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                customVideoView = view;
                customViewCallback = callback;
                root.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                addContentView(customVideoView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
            }

            @Override
            public void onHideCustomView() {
                hideCustomVideoView();
            }
        });
    }

    private void loadUrl(String url) {
        videoUrlInput.setText(url);
        webView.loadUrl(url);
    }

    private String normalizeUrl(String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return value;
        }
        if (value.startsWith("www.youtube.com") || value.startsWith("m.youtube.com")
                || value.startsWith("youtube.com") || value.startsWith("youtu.be")) {
            return "https://" + value;
        }
        return "https://m.youtube.com/results?search_query=" + value.replace(" ", "+");
    }

    private String carModeText() {
        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        int modeType = getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;
        boolean carMode = modeType == Configuration.UI_MODE_TYPE_CAR
                || (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR);
        return carMode ? "Car mode detected: follow parked-use rules." : "Phone/tablet mode detected.";
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        if (bold) {
            view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        }
        return view;
    }

    @Override
    public void onBackPressed() {
        if (customVideoView != null) {
            hideCustomVideoView();
            return;
        }
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    private void hideCustomVideoView() {
        if (customVideoView == null) {
            return;
        }
        ((ViewGroup) customVideoView.getParent()).removeView(customVideoView);
        customVideoView = null;
        root.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(0);
        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
            customViewCallback = null;
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
