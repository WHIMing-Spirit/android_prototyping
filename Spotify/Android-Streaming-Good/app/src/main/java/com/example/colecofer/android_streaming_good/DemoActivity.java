package com.example.colecofer.android_streaming_good;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class DemoActivity extends AppCompatActivity implements Player.NotificationCallback, ConnectionStateCallback {

    //Client Constants
    private static final String CLIENT_ID = "089d841ccc194c10a77afad9e1c11d54"; //TODO change this to our client_id
    private static final String REDIRECT_URI = "testschema://callback";         //TODO also change this
    private static final String[] SCOPES = new String[] {"user-read-private", "playlist-read", "playlist-read-private", "streaming"};

    //Test Constants
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        loginToSpotify();

    }



    public void loginToSpotify() {
        openLoginWindow();
    }


    public void updateView() {
        boolean loggedIn = isLoggedIn();

    }

    public boolean isLoggedIn() { return player != null && player.isLoggedIn(); }



    /* Authorization */
    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(SCOPES)
                .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;

                // Auth flow returned an error
                case ERROR:
                    log("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    log("Auth result: " + response.getType());
            }
        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        log("Got authentication token");
        if (player == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
            // the second argument in order to refcount it properly. Note that the method
            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
            // one passed in here. If you pass different instances to Spotify.getPlayer() and
            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
            player = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    log("-- Player initialized --");
                    player.setConnectivityStatus(OperationCallback, getNetworkConnectivity(DemoActivity.this));
                    player.addNotificationCallback(DemoActivity.this);
                    player.addConnectionStateCallback(DemoActivity.this);
                    // Trigger UI refresh
                    updateView();
                }

                @Override
                public void onError(Throwable error) {
                    log("Error in initialization: " + error.getMessage());
                }
            });
        } else {
            player.login(authResponse.getAccessToken());
        }
    }


    /**
     * Registering for connectivity changes in Android does not actually deliver them to
     * us in the delivered intent.
     *
     * @param context Android context
     * @return Connectivity state to be passed to the SDK
     */
    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }



    /* Overridden methods */

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
