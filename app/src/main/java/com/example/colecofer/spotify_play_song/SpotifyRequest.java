package com.example.colecofer.spotify_play_song;

import cz.msebera.android.httpclient.Header;
import android.util.Log;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

public class SpotifyRequest {

    //URLs
    private final String BASE_URL = "https://api.spotify.com/v1";
    private final String FEATURES_URL = "/audio-features/";
    private final String SEARCH_URL = "/search/";

    //Auth token TODO: Make this dynamic rather than hard-coded
    private final String BEARER_TOKEN = "BQDjt2a-H7AjET-yHYn_38GuiX1o0ah19STsauEE0EV8I5tYrrKi2K4gvsV-riOygDOXqcBFWl2I_n4kCppN70mK1ms2J1s07zzkr3-oISNWntLoF8IaztdKXbNgO28A75XtOUpiYnpKLr5xug";

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
     * @param query Name of the song
     * @param searchType A list of types to search for (e.g. "album, artist, playlist, track")
     * @param callback function that gets invoked after success or failure
     */
    public void searchSpotify(String query, String searchType, final SpotifyRequestCallBack callback) {
        String fullSearchURL = BASE_URL + SEARCH_URL;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + BEARER_TOKEN);

        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("type", searchType);

        client.get(fullSearchURL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("HTTP", "Status Code: " + statusCode);
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


    /**
     * Converts a String in JSON format to a JSONObject
     * @param jsonStr The string in JSON format
     * @return The JSONObject, or null if there was an error
     */
    public JSONObject convertStringToJSON(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            return jsonObj;
        } catch (JSONException e) {
            Log.d("HTTP", "Could not convert to JSONObject" + e.getMessage());
            return null;
        }
    }

}