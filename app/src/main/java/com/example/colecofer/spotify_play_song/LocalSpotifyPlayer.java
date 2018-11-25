package com.example.colecofer.spotify_play_song;

import android.content.BroadcastReceiver;
import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class LocalSpotifyPlayer implements Player.NotificationCallback, ConnectionStateCallback {


    //Constants
    private static final String CLIENT_ID = "089d841ccc194c10a77afad9e1c11d54";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final String TEST_SONG_URI = "spotify:track:6KywfgRqvgvfJc3JRwaZdZ";
    private static final String TEST_ALBUM_URI = "spotify:album:2lYmxilk8cXJlxxXmns1IU";
    private static final String TEST_PLAYLIST_URI = "spotify:user:spotify:playlist:2yLXxKhhziG2xzy7eyD4TD";
    private static final String TEST_QUEUE_SONG_URI = "spotify:track:5EEOjaJyWvfMglmEwf9bG3";

    private static final int REQUEST_CODE = 1337;              //I have no idea what this is
    public static final String TAG = "Spotify-Streaming-SDK";  //Logging tag

    private SpotifyPlayer player;                             //MUST be destroyed by calling SpotifyDestroyPlayer(Object) to avoid mem leaks
    private PlaybackState currentPlaybackState;
    private BroadcastReceiver networkStateReceiver;           //Used to get notifications from the system about the network state (need to set ACCESS_NETWORK_STATE permission

    private Metadata mMetadata;                               //Holds Spotify metadata about tracks and stuff


    private final Player.OperationCallback OperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            log("OK!");
        }

        @Override
        public void onError(Error error) {
            log("ERROR:" + error);
        }
    };



    public void onCreate() {

    }


    @Override
    public void onLoggedIn() {
        log("Login complete");
    }

    @Override
    public void onLoggedOut() {
        log("Logout complete");
    }

    @Override
    public void onLoginFailed(Error error) {
        log("On login failed: " + error);
    }

    @Override
    public void onTemporaryError() {
        log("Temporary error occured.");
    }

    @Override
    public void onConnectionMessage(String s) {
        log("On connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        log("onPlaybackEven happened");
    }

    @Override
    public void onPlaybackError(Error error) {
        log("onPlaybackError: " + error);
    }


    /**
     * Makes logging & debugging a little easier
     * @param message
     */
    public void log(String message) {
        Log.d(TAG, message);
    }

}
