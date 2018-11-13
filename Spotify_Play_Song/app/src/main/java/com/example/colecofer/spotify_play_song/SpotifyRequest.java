package com.example.colecofer.spotify_play_song;

import cz.msebera.android.httpclient.Header;
import android.util.Log;
import com.loopj.android.http.*;

public class SpotifyRequest {

    //URLs
    private final String BASE_URL = "https://api.spotify.com/v1";
    private final String FEATURES_URL = "/audio-features/";
    private final String SEARCH_URL = "/search/";

    //Auth token TODO: Make this dynamic rather than hard-coded
    private final String BEARER_TOKEN = "BQDbBoYzQpin4HNysrzhiqsffEdpd9QBSFwHXHhMaZSvu6YvFz4CLFny0B8ZkyznFay5EoL3spP9pus9JgVmK5ySdxyWRQbDTB8JAkkjX6k6m4KcpafGNOy_F979nqvyurC35mzD5D9n9VBMtg";

    /**
     * Calls the Spotify features API end-point for the passed in trackID.
     * TODO: Remove hard coded auth token
     * @param trackID The ID for any given Spotify song
     * @param callback function that gets invoked after success or failure
     */
    public void getFeaturesFromTrackID(String trackID, final SpotifyRequestCallBack callback) {
        String fullFeaturesURL = BASE_URL + FEATURES_URL + trackID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + BEARER_TOKEN);

        client.get(fullFeaturesURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }


            @Override
            public void onStart() {
                Log.d("HTTP", "Request is starting...");
                //Doesn't have to be overridden
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("HTTP", "Request is retrying...");
                //Doesn't have to be overridden
            }
        });
    }


    /**
     * Calls the Spotify search API end-point and returns the JSON response of search
     * queries through the callback function.
     * TODO: Remove hard coded auth token
     * @param trackName Name of the song
     * @param callback function that gets invoked after success or failure
     */
    public void searchForTrack(String trackName, final SpotifyRequestCallBack callback) {
        String fullSearchURL = BASE_URL + FEATURES_URL;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + BEARER_TOKEN);

        //TODO: Use trackName but format it first
        client.addHeader("q", trackName);
        client.addHeader("type", "artist");

        client.get(fullSearchURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }


            @Override
            public void onStart() {
                Log.d("HTTP", "Request is starting...");
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("HTTP", "Request is retrying...");
            }
        });

    }


}