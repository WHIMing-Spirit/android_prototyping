package com.example.colecofer.spotify_play_song;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class GeniusRequest {

    private final String BASE_URL = "http://api.genius.com";
    private final String SEARCH_URL = "/search";
    private final String AUTH_TOKEN = "6ON6dkirH-hoT3XeA000N1XPwam6vbogz0o0pFikY8DnMOhEaNpnPKly-QigSSOm";

    /**
     * This will be useful inorder to construct a URL for a song, which will be
     * used to scrape lyrics from.
     * @param songName The name of the song to get lyrics from
     */
    public void searchForSong(String songName) {
        String fullSearchURL = BASE_URL + SEARCH_URL;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer " + AUTH_TOKEN);

        RequestParams params = new RequestParams();
        params.put("q", songName);

        client.get(fullSearchURL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("HTTP", "Success - Status Code: " + statusCode);
                Log.d("HTTP", "Response: " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("HTTP", "*** Failure - Status Code: " + statusCode);
                //callback.spotifyResponse(false, responseString);
            }

        });
    }

}
