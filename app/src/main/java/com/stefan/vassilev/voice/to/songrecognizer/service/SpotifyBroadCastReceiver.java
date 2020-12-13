package com.stefan.vassilev.voice.to.songrecognizer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class SpotifyBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = SpotifyBroadCastReceiver.class.getName();

    private final ArduinoService arduinoService;

    public SpotifyBroadCastReceiver(ArduinoService arduinoService) {
        this.arduinoService = arduinoService;
    }

    static final class BroadcastTypes {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.i(TAG, "Received new action: " + action);
        switch (action) {
            case BroadcastTypes.METADATA_CHANGED:
                String artistName = intent.getStringExtra("artist");
                String albumName = intent.getStringExtra("album");
                String trackName = intent.getStringExtra("track");
                int trackLengthInSec = intent.getIntExtra("length", 0);

                arduinoService.setCurrentPlayingTrack(trackName, artistName, trackLengthInSec);
            case BroadcastTypes.PLAYBACK_STATE_CHANGED:
                boolean playing = intent.getBooleanExtra("playing", false);
                int positionInMs = intent.getIntExtra("playbackPosition", 0);
                Log.i(TAG, "Song play status changed");
            default:
                Log.w(TAG, "Event not being processed: " + action);
        }

    }
}
