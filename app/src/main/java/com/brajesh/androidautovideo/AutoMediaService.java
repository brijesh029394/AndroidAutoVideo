package com.brajesh.androidautovideo;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoMediaService extends MediaBrowserServiceCompat {
    private static final String ROOT_ID = "android_auto_video_root";
    private static final String INFO_CATEGORY_ID = "android_auto_video_info";
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
                startItem(mediaId);
            }

            @Override
            public void onPlay() {
                startItem(HELP_ID);
            }

            @Override
            public void onPause() {
                setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            }

            @Override
            public void onStop() {
                setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
            }
        });
        mediaSession.setQueue(Collections.singletonList(
                new MediaSessionCompat.QueueItem(
                        buildDescription(HELP_ID, "Android Auto Audio", "Tap to confirm the media service is visible."),
                        1
                )
        ));
        mediaSession.setQueueTitle("Android Auto Audio");
        updateMetadata(HELP_ID);
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        mediaSession.setActive(true);
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
            items.add(buildBrowsable(
                    INFO_CATEGORY_ID,
                    "App status",
                    "Open for Android Auto compatibility checks."
            ));
        } else if (INFO_CATEGORY_ID.equals(parentId)) {
            items.add(buildPlayable(
                    HELP_ID,
                    "Android Auto media service ready",
                    "This confirms the supported media path is available."
            ));
            items.add(buildPlayable(
                    PARKED_ID,
                    "YouTube launcher stays on phone",
                    "Open the phone app while parked for video links."
            ));
        }
        result.sendResult(items);
    }

    private MediaBrowserCompat.MediaItem buildBrowsable(String mediaId, String title, String subtitle) {
        return new MediaBrowserCompat.MediaItem(
                buildDescription(mediaId, title, subtitle),
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        );
    }

    private MediaBrowserCompat.MediaItem buildPlayable(String mediaId, String title, String subtitle) {
        return new MediaBrowserCompat.MediaItem(
                buildDescription(mediaId, title, subtitle),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        );
    }

    private MediaDescriptionCompat buildDescription(String mediaId, String title, String subtitle) {
        return new MediaDescriptionCompat.Builder()
                .setMediaId(mediaId)
                .setTitle(title)
                .setSubtitle(subtitle)
                .build();
    }

    private void startItem(String mediaId) {
        updateMetadata(mediaId);
        setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
    }

    private void updateMetadata(String mediaId) {
        String title = PARKED_ID.equals(mediaId) ? "Use phone screen while parked" : "Android Auto Audio";
        String subtitle = PARKED_ID.equals(mediaId)
                ? "Video playback is not exposed on Android Auto projection."
                : "Media shell connected.";
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
                .build());
    }

    private void setPlaybackState(int state) {
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_STOP)
                .setState(state, 0, 1.0f)
                .build());
    }
}
