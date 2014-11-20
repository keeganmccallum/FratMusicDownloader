package com.keeganmccallum.fratmusicdownloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;

import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.client.OkClient;

/**
 * Created by keeganmccallum on 15/11/14.
 */
public class FratMusic {
    public final static String BASE_URL = "http://fratmusic.com/api/";
    public final static String STATIONS_URL = BASE_URL+"stations.php";
    public final static String PLAYLISTS_URL = BASE_URL+"playlists.php";

    public final static OkHttpClient client = new OkHttpClient();

    public static JSONArray getStations() throws Exception {
        return getRequest(STATIONS_URL).getJSONArray("stations");
    }

    public static JSONArray getPlaylists(int stationId) throws Exception {
        return getRequest(PLAYLISTS_URL+"?stationId="+stationId+"&playlistPerPage=10000")
               .getJSONArray("playlists");
    }

    public static JSONArray getSongs(String playlistURL) throws Exception {
        return getRequest(playlistURL).getJSONObject("playlist_mix").getJSONArray("tracks");
    }

    public static void downloadPlaylist(final Activity activity, final JSONObject playlist) {
        new AsyncTask<JSONArray, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(JSONArray... params) {
                JSONArray songs = null;
                try {
                    songs = FratMusic.getSongs(playlist.getString("playlist_api_url"));
                    for (int i = 0; i < songs.length(); i++) {
                        String url = songs.getJSONObject(i).getString("stream_url");
                        String filename = songs.getJSONObject(i).getString("track_name");
                        String playlistName = playlist.getString("playlist_name");
                        downloadSong(activity, url, playlistName, filename);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return songs;
            }
        }.execute();
    }

    public static void downloadSong(Activity activity, String url, String playlistName, String filename) {
        DownloadManager dm = (DownloadManager) activity.getSystemService(Activity.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String path = "/Music/FratMusic/"+ playlistName.replace("/", "|");
        request.setDestinationInExternalPublicDir(path, filename.replace("/", "|") + ".mp3");
        dm.enqueue(request);
    }

    public static JSONObject getRequest(String url) throws Exception {
        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }
}
