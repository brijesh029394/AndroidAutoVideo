package com.brajesh.androidautovideo;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

public class AutoMediaService extends MediaBrowserServiceCompat {
    private static final String ROOT_ID = "android_auto_video_root";
    private static final String HELP_ID = "android_auto_video_help";
    private static final String PARKED_ID = "android_auto_video_parked";

    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "AndroidAutoVideo");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                updateMetadata(mediaId);
            }

            @Override
            public void onPlay() {
                updateMetadata(HELP_ID);
            }

            @Override
            public void onPause() {
                updateMetadata(HELP_ID);
            }
        });
        setSessionToken(mediaSession.getSessionToken());
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.release();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        if (ROOT_ID.equals(parentId)) {
            items.add(buildPlayable(
                    HELP_ID,
                    "Android Auto ready",
                    "This app appears through the supported media path."
            ));
            items.add(buildPlayable(
                    PARKED_ID,
                    "YouTube launcher is on phone",
                    "Open the phone app while parked to launch YouTube links."
            ));
        }
        result.sendResult(items);
    }

    @Override
    public void onLoadItem(@NonNull String itemId, @NonNull Result<MediaBrowserCompat.MediaItem> result) {
        if (HELP_ID.equals(itemId)) {
            result.sendResult(buildPlayable(
                    HELP_ID,
                    "Android Auto ready",
                    "This app appears through the supported media path."
            ));
            return;
        }

        if (PARKED_ID.equals(itemId)) {
            result.sendResult(buildPlayable(
                    PARKED_ID,
                    "YouTube launcher is on phone",
                    "Open the phone app while parked to launch YouTube links."
            ));
            return;
        }

        result.sendResult(null);
    }

    private MediaBrowserCompat.MediaItem buildPlayable(String mediaId, String title, String subtitle) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(mediaId)
                .setTitle(title)
                .setSubtitle(subtitle)
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    private void updateMetadata(String mediaId) {
        String title = PARKED_ID.equals(mediaId) ? "Use phone screen while parked" : "Android Auto Video";
        String subtitle = PARKED_ID.equals(mediaId)
                ? "Video playback is not exposed on Android Auto projection."
                : "Media shell connected.";
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
                .build());
        mediaSession.setActive(true);
    }
}
