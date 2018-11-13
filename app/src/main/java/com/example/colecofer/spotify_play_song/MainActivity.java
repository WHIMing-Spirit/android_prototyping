
package com.example.colecofer.spotify_play_song;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String pukeChamberID = "2JroNLGqmuroC16iktbwBU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
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

        //request.playPauseSong("play");
        //request.playPauseSong("pause");

    }


    public void initUI() {
        final SpotifyRequest request = new SpotifyRequest();

        //Setup play button listeners
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.playPauseSong("play");
            }
        });

        //Setup pause button listeners
        Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SpotifyRequest request = new SpotifyRequest();
                request.playPauseSong("pause");
            }
        });
    }




}
