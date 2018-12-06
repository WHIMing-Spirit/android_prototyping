package com.example.colecofer.android_streaming_good;

import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SpotifyRequest {

    //URLs
    private final String BASE_URL = "https://api.spotify.com/v1";
    private final String FEATURES_URL = "/audio-features/";
    private final String SEARCH_URL = "/search/";
    private final String PLAYER_URL = "/me/player/";
    private final String ARTIST_URL = "/artists/";
    private final String ALBUM_URL = "";
    private final String COVER_ART_URL = "";
    private final String AUTH_URL = "https://accounts.spotify.com/api/token";

    //WHIM Credentials
    //    private final String CLIENT_ID = "e55495216aef49088a8b9dbad6cdeba4";
    //    private final String CLIENT_SECRET = "b3461bbeedd34cbb9fed2be80c846fab";

    //Personal Dev Credentials
    private final String CLIENT_ID = "5f0eac9db12042cfa8b9fb95b0f3f4d8";
    private final String CLIENT_SECRET = "4f0d128f8f1b4776a530292cdef1dd45";

    //Temporary Token until scopes are added to authorization
    private final String TOKEN = "BQCKHspteexTfDBfClUd_3CJp7m3Y6guYG-_Zu_2v03HGfVAmFs-HQQ240adrk2WImPZWoxTSO35EyxfXTgqw55Yls5G1JyOhfK7kCRLb3j1WXcYP3_iZ-bdHcyjk9jd2iIg7pbYZm3rUBckigsTwMzHWZy2OYhv";

    /**
     * Gets the authorization token given the clientid and clientsecret
     * @param scopes
     * @param callback
     */
    public void getAuthToken(String scopes, final SpotifyRequestCallBack callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams body = new RequestParams();
        body.put("client_id", CLIENT_ID);
        body.put("client_secret", CLIENT_SECRET);
        body.put("grant_type", "client_credentials");
        body.put("scope", scopes);

        client.post(AUTH_URL, body, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }


    /**
     * Provides the artist name given the Spotify ID
     * @param trackID The Spotify / Track ID
     * @param authToken The authentication token
     * @param callback Callback function to be implemented
     */
    public void getArtistName(String trackID, String authToken, final SpotifyRequestCallBack callback) {
        String fullArtistURL = BASE_URL + ARTIST_URL + trackID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authToken);

        client.get(fullArtistURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }

    public void getAlbumName() {

    }

    public void getCoverArt() {

    }

    /**
     * Calls the Spotify features API end-point for the passed in trackID.
     * TODO: Remove hard coded auth token
     * @param trackID The ID for any given Spotify song
     * @param callback function that gets invoked after success or failure
     */
    public void getFeaturesFromTrackID(String trackID, String authToken, final SpotifyRequestCallBack callback) {
        String fullFeaturesURL = BASE_URL + FEATURES_URL + trackID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + authToken);

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
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("HTTP", "Request is retrying...");
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
    public void searchSpotify(String query, String searchType, String authToken, final SpotifyRequestCallBack callback) {
        String fullSearchURL = BASE_URL + SEARCH_URL;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + authToken);

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
     * Plays or pauses the track currently playing on the users active spotify player.
     * @param action a string containing either "play" or "pause"
     */
    public void playPauseSong(final String action, String authToken) {
        String fullURL = BASE_URL + PLAYER_URL+ action;
        AsyncHttpClient client = new AsyncHttpClient();
//        client.addHeader("Authorization", "Bearer " + authToken);
        client.addHeader("Authorization", "Bearer " + TOKEN);

        client.put(fullURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("HTTP", action + " was successful! Status Code: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("HTTP", "Error, could not play/pause track: " + responseString);
            }

        });
    }


    /**
     * Converts a String in JSON format to a JSONObject
     * @param jsonStr The string in JSON format
     * @return The JSONObject, or null if there was an error
     */
    public static JSONObject convertStringToJSON(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            return jsonObj;
        } catch (JSONException e) {
            Log.d("HTTP", "Could not convert to JSONObject" + e.getMessage());
            return null;
        }
    }

}