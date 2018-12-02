package com.example.colecofer.spotify_play_song;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MusixmatchRequest {

    private static final String MATCHER_URL = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get";

    public void getLyrics(final MusixmatchRequestCallBack callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("format", "json");
        params.put("callback", "callback");
        params.put("q_track", "Hynpotize");
        params.put("q_artist", "Notorious B.I.G.");
        params.put("callback", "jsonp");
        params.put("apikey", "5ad66be966fed184e1e2a939de699f22");

        client.get(MATCHER_URL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.MusixmatchResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.MusixmatchResponse(false, responseString);
            }

        });
    }

}
