
package com.example.colecofer.spotify_play_song;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private final String pukeChamberID = "2JroNLGqmuroC16iktbwBU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        LocalSpotifyPlayer spotifyPlayer = new LocalSpotifyPlayer();
//        spotifyPlayer.onCreate();


//        testSpotifyAPI();
        testMusixmatch();
    }

    public void testMusixmatch() {
        final MusixmatchRequest request = new MusixmatchRequest();
        request.getLyrics(new MusixmatchRequestCallBack() {

            @Override
            public void MusixmatchResponse(boolean success, String response) {
                try {
                    String json = SpotifyRequest.convertStringToJSON(response).getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").getString("lyrics_body");
                    Log.d("Musixmatch", "After conversion: " + json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void testSpotifyAPI() {
        final SpotifyRequest request = new SpotifyRequest();
        String authScopes = "user-read-playback-state user-modify-playback-state";

        //Get the auth token given the scope
        request.getAuthToken(authScopes, new SpotifyRequestCallBack() {

            @Override
            public void spotifyResponse(boolean success, String response) {
                Log.d("HTTP", "Auth token: " + getAuthFromJSON(response));

                String authToken = getAuthFromJSON(response);
                initUI(authToken);

                //Get the features for a track ID
                request.getFeaturesFromTrackID(pukeChamberID, authToken, new SpotifyRequestCallBack() {
                    @Override
                    public void spotifyResponse(boolean success, String response){
                        Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
                    }
                });

                //Get the search results given a query and search type
                request.searchSpotify("Slow Torture Puke Chamber","track", authToken, new SpotifyRequestCallBack() {
                    @Override
                    public void spotifyResponse(boolean success, String response) {
                        Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
                    }
                });

            }
        });



    }


    /**
     * Setup the user interface elements
     */
    public void initUI(final String authToken) {
        final SpotifyRequest request = new SpotifyRequest();

        //Setup play button listener
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.playPauseSong("play", authToken);
            }
        });

        //Setup pause button listener
        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.playPauseSong("pause", authToken);
            }
        });

        //Setup trackID textview
        final TextView searchSpotifyTextField = findViewById(R.id.querySpotifyTextField);
        searchSpotifyTextField.setHint("Enter Track ID");

        //Setup start song button listener
        Button startSongButton = findViewById(R.id.startSongButton);
        startSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackIdInput = searchSpotifyTextField.getText().toString();
                Log.d("HTTP","Track Id from input: " + trackIdInput);
                //TODO: Call the API to start a song via ID
            }
        });

    }

    /**
     * Parses a json block returned from the auth endpoint and returns the auth token
     * @param responseJSON The json block from the auth endpoint
     * @return The auth token
     */
    public String getAuthFromJSON(String responseJSON) {
        JSONObject json = SpotifyRequest.convertStringToJSON(responseJSON);
        String authToken = "Failed to get auth token";
        try {
            authToken = json.getString("access_token");
        } catch (JSONException e) {
            Log.d("HTTP", "Error - Could not extract auth token from response" + e.getMessage());
        }
        return authToken;
    }
}
