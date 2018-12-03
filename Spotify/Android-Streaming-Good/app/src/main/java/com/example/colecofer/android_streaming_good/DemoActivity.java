package com.example.colecofer.android_streaming_good;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.VolumeShaper;
import android.os.StrictMode;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class DemoActivity extends AppCompatActivity implements Player.NotificationCallback, ConnectionStateCallback {


    //TODO: Authenticate with the new Spotify account (first need to create a new client_id / secret)
    //private static final String CLIENT_ID = "5f0eac9db12042cfa8b9fb95b0f3f4d8";     //Cole's personal client
    //private static final String REDIRECT_URI = "whimvisualizer://callback ";       //Cole's personal redirect uri

    //Client Constants
    private static final String CLIENT_ID    = "089d841ccc194c10a77afad9e1c11d54";       //Spotifys test
    private static final String REDIRECT_URI = "testschema://callback";                  //Spotifys test

    //Authentication access scopes
    private static final String[] SCOPES = new String[] {"user-read-private", "playlist-read", "playlist-read-private", "streaming"};

    //Test Constants
    private static final String TEST_SONG_URI       = "spotify:track:6KywfgRqvgvfJc3JRwaZdZ";
    private static final String HYP_TRACK_URI       = "spotify:track:7KwZNVEaqikRSBSpyhXK2j";
    private static final String TEST_ALBUM_URI      = "spotify:album:2lYmxilk8cXJlxxXmns1IU";
    private static final String TEST_PLAYLIST_URI   = "spotify:user:spotify:playlist:2yLXxKhhziG2xzy7eyD4TD";
    private static final String TEST_QUEUE_SONG_URI = "spotify:track:5EEOjaJyWvfMglmEwf9bG3";

    //TODO: Find out what a request_code is...
    private static final int REQUEST_CODE = 1337;              //I think it has to do with verification of which Activity authentication happens at

    public static final String TAG        = "Spotify";         //Logcat Tag

    private SpotifyPlayer player;                              //Spotify player that controls the background service
    private PlaybackState currentPlaybackState;                //The current state of the player (e.g. is it playing music or not?)
    private BroadcastReceiver networkStateReceiver;            //Used to get notifications from the system about the network state (need to set ACCESS_NETWORK_STATE permission)

    private Metadata metadata;                                 //Holds Spotify metadata about tracks and stuff


    //Callback functions for playback events
    private final Player.OperationCallback OperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            log("Callback: Success!");
        }

        @Override
        public void onError(Error error) {
            log("Callback ERROR:" + error);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        loginToSpotify();
    }


    /**
     * Starts the authentication process by redirecting the user into
     * their browser to log into Spotify.
     */
    public void loginToSpotify() {
        initUI();
        openLoginWindow();
    }


    /**
     * Should be invoked when visible information changes within the view.
     */
    public void updateView() {

        if (metadata != null) {
            if (metadata.currentTrack != null) {
                TextView albumText = findViewById(R.id.albumNameTextView);
                TextView artistNameText = findViewById(R.id.artistNameTextView);

                albumText.setText("Artist: " + metadata.currentTrack.artistName);
                artistNameText.setText("Album: " + metadata.currentTrack.albumName);
            } else {
                log("There is no track playing currently (This is only an error if you expected it to be playing)");
            }

        }
    }


    /**
     * Check if there is a user logged into the player
     * @return True if there is someone logged in.
     */
    public boolean isLoggedIn() {
        return player != null && player.isLoggedIn();
    }


    /**
     * Initialize the user interface including listeners.
     */
    public void initUI() {

        //Play button
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log("Starting plaback for " + HYP_TRACK_URI);
                if (!player.getPlaybackState().isPlaying) {
                    setButtonText(R.id.pauseButton, "Pause");
                }
                player.playUri(OperationCallback, HYP_TRACK_URI, 0, 0);
                currentPlaybackState = player.getPlaybackState();
                updateView();
                setCoverArt();
            }
        });

        //Pause / Resume button
        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPlaybackState = player.getPlaybackState();
                if (currentPlaybackState != null && currentPlaybackState.isPlaying) {
                    log("Playback has been paused");
                    setButtonText(R.id.pauseButton, "Resume");
                    player.pause(OperationCallback);
                } else {
                    log("Playback has been resumed");
                    setButtonText(R.id.pauseButton, "Pause");
                    player.resume(OperationCallback);
                }

            }
        });


        //Search Image Button
        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPlaybackState = player.getPlaybackState();
                EditText trackEditText = findViewById(R.id.trackEditText);
                String trackString = trackEditText.getText().toString();
                
            }
        });

    }


    //TODO: Update to ASYNC task
    //TODO: Invoke setting album art when finished
    private void setCoverArt() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    log("album: " + metadata.currentTrack.albumCoverWebUrl);

                    try {
                        ImageView i = findViewById(R.id.albumArtImageView);
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(metadata.currentTrack.albumCoverWebUrl).getContent());
                        i.setImageBitmap(bitmap);
                        log("Image loading was successfull I think....");
                    } catch (MalformedURLException e) {
                        log("Error loading cover art: MalformedURLException");
                        e.printStackTrace();
                    } catch (IOException e) {
                        log("Error loading cover art: IOExcetion");
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    /**
     * Sets the text for a button
     * @param id The id of the button (e.g. R.id.pauseButton)
     * @param text The updated text
     */
    private void setButtonText(int id, String text) {
        ((Button) findViewById(id)).setText(text);
    }


    /**
     * Redirects the user to the Spotify login page in their local browser
     */
    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(SCOPES)
                .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

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

    /**
     * Authentication was successful, and the player can be initialized
     * @param authResponse
     */
    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        log("Got authentication token");
        if (player == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            player = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {

                @Override
                public void onInitialized(SpotifyPlayer player) {
                    log("Player has been initialized");
                    player.setConnectivityStatus(OperationCallback, getNetworkConnectivity(DemoActivity.this));
                    player.addNotificationCallback(DemoActivity.this);
                    player.addConnectionStateCallback(DemoActivity.this);

                    updateView(); //TODO: This may not be necessary (?)
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


    /**
     * Is invoked when ever a playback event occurs (e.g. play / pause)
     * @param event
     */
    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        //log("Event: " + event);
        currentPlaybackState = player.getPlaybackState();
        metadata = player.getMetadata();
        //Log.i(TAG, "Player state: " + currentPlaybackState);
        //Log.i(TAG, "Metadata: " + metadata);
        updateView();
    }



    /* Overridden flow methods */

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


    /**
     * *** ULTRA-IMPORTANT ***
     * ALWAYS call this in your onDestroy() method, otherwise you will leak native resources!
     * This is an unfortunate necessity due to the different memory management models of
     * Java's garbage collector and C++ RAII.
     */
    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
