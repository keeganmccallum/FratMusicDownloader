package com.keeganmccallum.fratmusicdownloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by keeganmccallum on 18/11/14.
 */
public class StationFragment extends FratMusicFragment {
    private JSONArray playlists = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(listView);
    }

    @Override
    protected void getData() {
        final int stationId = getArguments().getInt("stationId");
        new AsyncTask<JSONArray, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(JSONArray... params) {

                try {
                    playlists = FratMusic.getPlaylists(stationId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return playlists;
            }

            @Override
            protected void onPostExecute(JSONArray playlists) {
                super.onPostExecute(playlists);
                for (int i = 0; i < playlists.length(); i++) {
                    try {
                        adapter.add(playlists.getJSONObject(i).getString("playlist_name"));
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
            String playlistURL = playlists.getJSONObject(position).getString("playlist_api_url");
            PlaylistFragment newFragment = new PlaylistFragment();
            Bundle args = new Bundle();
            args.putString("playlistURL", playlistURL);
            newFragment.setArguments(args);

            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.container, newFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==listView.getId()) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.download:
                try {
                    FratMusic.downloadPlaylist(getActivity(), playlists.getJSONObject(info.position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
