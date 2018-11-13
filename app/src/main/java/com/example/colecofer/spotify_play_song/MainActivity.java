
package com.example.colecofer.spotify_play_song;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final String pukeChamberID = "2JroNLGqmuroC16iktbwBU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testSpotifyAPI();
    }


    public void testSpotifyAPI() {
        SpotifyRequest request = new SpotifyRequest();

        //Passes in the implementation of the callback function that is defined in the SpotifyRequestCallBack interface
        request.getFeaturesFromTrackID(pukeChamberID, new SpotifyRequestCallBack() {
            @Override
            public void spotifyResponse(boolean success, String response){
                Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
            }
        });

        request.searchSpotify("Slow Torture Puke Chamber","track", new SpotifyRequestCallBack() {
            @Override
            public void spotifyResponse(boolean success, String response) {
                Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
            }
        });

        request.playPauseSong("play");
//        request.playPauseSong("pause");

    }


}