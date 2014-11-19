package com.keeganmccallum.fratmusicdownloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by keeganmccallum on 18/11/14.
 */
public class RootFragment extends FratMusicFragment{
    private JSONArray stations = null;

    @Override
    protected void getData() {
        new AsyncTask<JSONArray, String, JSONArray>() {
            @Override
            protected JSONArray doInBackground(JSONArray... params) {

                try {
                    stations = FratMusic.getStations();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return stations;
            }

            @Override
            protected void onPostExecute(JSONArray stations) {
                super.onPostExecute(stations);
                for (int i = 0; i < stations.length(); i++) {
                    try {
                        adapter.add(stations.getJSONObject(i).getString("station_name"));
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
            int stationId = stations.getJSONObject(position).getInt("station_id");
            StationFragment newFragment = new StationFragment();
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            newFragment.setArguments(args);

            getActivity().getFragmentManager().beginTransaction()
                         .replace(R.id.container, newFragment)
                         .addToBackStack(null)
                         .commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
