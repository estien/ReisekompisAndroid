package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopsAdapter;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.configuration.ReisekompisService;
import info.reisekompis.reisekompis.fragments.FindStopsFragment;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;

public class FindStopsActivity extends Activity implements OnListItemSelectedListener {
    private HttpClient httpClient;
    private ProgressBar searchingProgressBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_stops);
        httpClient = new HttpClient();
        searchingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_searching_old);
        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        /*
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new FindStopsFragment())
                .commit();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemsSelected(List<TransportationType> types) {
        ObjectMapper mapper = new ObjectMapper();

        TransportationType[] existingTransportationTypes = GetStoredTransportationTypes(mapper);
        if(existingTransportationTypes.length > 0) {
            types = MergeExistingWithSelectedTranportationTypes(existingTransportationTypes, types);
        }

        String json = null;
        try {
            json = mapper.writeValueAsString(types);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, json);
        editor.commit();
    }

    private List<TransportationType> MergeExistingWithSelectedTranportationTypes(TransportationType[] existingTransportationTypes, List<TransportationType> selectedLines) {
        return selectedLines; // TODO implement
    }

    private void AddAllLines(HashSet<Line> allLines, TransportationType[] types) {
        for (TransportationType type : types) {
            for (Stop stop : type.getStops()) {
                for(Line line : stop.getLines()) {
                    allLines.add(line);
                }
            }
        }

    }

    private TransportationType[] GetStoredTransportationTypes(ObjectMapper mapper) {
        try {
            TransportationType[] transportationTypes = mapper.readValue(sharedPreferences.getString(Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES, null), TransportationType[].class);// TODO make sure is not null
            return transportationTypes != null ? transportationTypes : new TransportationType[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TransportationType[0];
    }

    class SearchStopsAsyncTask extends AsyncTask<String, Void, Stop[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Stop[] doInBackground(String... params) {
            String jsonResponseString = httpClient.get(params[0]);
            ObjectMapper mapper = new ObjectMapper();
            Stop[] result;
            try {
                result = mapper.readValue(jsonResponseString, Stop[].class);
            } catch (IOException e) {
                e.printStackTrace();
                return new Stop[0];
            }
            return result;
        }

        @Override
        protected void onPostExecute(Stop[] result) {
            searchingProgressBar.setVisibility(View.INVISIBLE);
            FindStopsFragment fragment = (FindStopsFragment) getFragmentManager().findFragmentById(R.id.main_fragment_container);
            StopsAdapter adapter = new StopsAdapter(FindStopsActivity.this, R.layout.stop_list_item, result);
            fragment.setListAdapter(adapter);
        }
    }
}
