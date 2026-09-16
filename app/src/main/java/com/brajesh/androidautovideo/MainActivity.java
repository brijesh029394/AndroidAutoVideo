package com.brajesh.androidautovideo;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String DEFAULT_VIDEO_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    private EditText videoUrlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildContentView());
    }

    private View buildContentView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(18, 18, 20));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(28));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(root);

        TextView title = text("Android Auto Video", 28, true);
        root.addView(title);

        TextView subtitle = text(
                "Parked/passenger-safe YouTube launcher. This app does not bypass Android Auto driving restrictions.",
                16,
                false
        );
        subtitle.setPadding(0, dp(12), 0, dp(20));
        root.addView(subtitle);

        TextView status = text(carModeText(), 15, false);
        status.setTextColor(Color.rgb(120, 210, 160));
        status.setPadding(0, 0, 0, dp(18));
        root.addView(status);

        videoUrlInput = new EditText(this);
        videoUrlInput.setSingleLine(false);
        videoUrlInput.setMinLines(2);
        videoUrlInput.setText(DEFAULT_VIDEO_URL);
        videoUrlInput.setHint("Paste YouTube URL");
        videoUrlInput.setTextColor(Color.WHITE);
        videoUrlInput.setHintTextColor(Color.rgb(160, 160, 165));
        videoUrlInput.setBackgroundColor(Color.rgb(35, 35, 40));
        videoUrlInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        root.addView(videoUrlInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        Button openButton = new Button(this);
        openButton.setText("Open YouTube Video");
        openButton.setAllCaps(false);
        openButton.setOnClickListener(v -> openVideo());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dp(18), 0, dp(18));
        root.addView(openButton, buttonParams);

        TextView note = text(
                "Use only when the vehicle is parked or when a passenger display allows it. For driving, use audio-safe Android Auto media controls instead.",
                14,
                false
        );
        note.setTextColor(Color.rgb(210, 210, 215));
        root.addView(note);

        return scrollView;
    }

    private void openVideo() {
        String url = videoUrlInput.getText().toString().trim();
        if (!url.startsWith("https://www.youtube.com/")
                && !url.startsWith("https://youtu.be/")
                && !url.startsWith("https://m.youtube.com/")) {
            Toast.makeText(this, "Please enter a valid YouTube URL.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception exception) {
            Toast.makeText(this, "No app found to open this video.", Toast.LENGTH_SHORT).show();
        }
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

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
