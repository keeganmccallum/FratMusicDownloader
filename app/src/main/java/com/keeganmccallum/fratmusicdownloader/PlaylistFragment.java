package com.keeganmccallum.fratmusicdownloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by keeganmccallum on 18/11/14.
 */
public class PlaylistFragment extends FratMusicFragment {
    private JSONArray songs = null;

    @Override
    protected void getData() {
        final String playlistURL = getArguments().getString("playlistURL");
        new AsyncTask<JSONArray, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(JSONArray... params) {

                try {
                    songs = FratMusic.getSongs(playlistURL);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return songs;
            }

            @Override
            protected void onPostExecute(JSONArray songs) {
                super.onPostExecute(songs);
                for (int i = 0; i < songs.length(); i++) {
                    try {
                        adapter.add(songs.getJSONObject(i).getString("track_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            String url = songs.getJSONObject(position).getString("stream_url");
            String filename = songs.getJSONObject(position).getString("track_name");
            Log.d("url", url);
            FratMusic.downloadSong(getActivity(), url, "", filename);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
