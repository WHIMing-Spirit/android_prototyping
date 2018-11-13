
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

        SpotifyRequest request = new SpotifyRequest();

        //Passes in the implementation of the callback function in the interface SpotifyRequestCallBack
        request.getFeaturesFromTrackID(pukeChamberID, new SpotifyRequestCallBack() {
            @Override
            public void spotifyResponse(boolean success, String response){
                Log.d("\nHTTP", " ** FEATURE **\n");
                Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
            }
        });


//        request.searchForTrack("The%20Faceless", new SpotifyRequestCallBack() {
//            @Override
//            public void spotifyResponse(boolean success, String response) {
//                Log.d("\nHTTP", " ** SEARCH **\n");
//                Log.d("HTTP", "Success: " + success + "\nResponse: " + response);
//            }
//        });

    }
}
