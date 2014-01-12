package info.reisekompis.reisekompis.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopsAdapter;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.configuration.ReisekompisService;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;
import static java.util.Arrays.asList;

public class SubscribeToLinesActivity extends ListActivity implements OnListItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    protected HttpClient httpClient;
    MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe);
        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        httpClient = new HttpClient();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        handleSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscribe, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        assert searchMenuItem != null;
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        assert searchView != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.requestFocus();
        searchMenuItem.expandActionView();
        return true;
    }

    @Override
    public void onListItemsSelected(List<TransportationType> types) {
        ObjectMapper mapper = new ObjectMapper();

        TransportationType[] existingTransportationTypes = GetStoredTransportationTypes(mapper);
        if(existingTransportationTypes.length > 0) {
            types = mergeExistingWithSelectedTranportationTypes(existingTransportationTypes, types);
        }

        String json = null;
        try {
            json = mapper.writeValueAsString(types);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, json);
        editor.commit();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private List<TransportationType> mergeExistingWithSelectedTranportationTypes(TransportationType[] existingTransportationTypes, List<TransportationType> selectedLines) {
        return selectedLines; // TODO implement
    }

    private TransportationType[] GetStoredTransportationTypes(ObjectMapper mapper) {
        try {
            String value = sharedPreferences.getString(Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
            if (value != null) {
                TransportationType[] transportationTypes = mapper.readValue(value, TransportationType[].class);
                return transportationTypes != null ? transportationTypes : new TransportationType[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TransportationType[0];
    }

    private boolean isSearching() {
        return Intent.ACTION_SEARCH.equals(getIntent().getAction());
    }

    private void handleSearch() {
        if (isSearching()) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            if (query != null) {
                query = query.trim();
                if(query.length() < 4) return; // TODO: add dialog informing about minimum search length
                String searchQuery = ReisekompisService.SEARCH + query;
                new SearchStopsAsyncTask().execute(searchQuery);
            }
        }
    }

    class SearchStopsAsyncTask extends AsyncTask<String, Void, Stop[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarVisible(true);
        }

        @Override
        protected Stop[] doInBackground(String... params) {
            String url = params[0].replace(" ", "%20");
            String jsonResponseString = getHttpClient().get(url);
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
            setProgressBarVisible(false);
            searchMenuItem.collapseActionView();
            StopsAdapter adapter = new StopsAdapter(getApplicationContext(), R.layout.stop_list_item, result);
            setListAdapter(adapter);
        }
    }

    public void setProgressBarVisible(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Stop clickedStop = (Stop) getListAdapter().getItem(position);
        List<String> lines = new ArrayList<String>();
        for (Line line : clickedStop.getLines()) {
            lines.add(line.getName());
        }
        final List<String> linesToAdd = new ArrayList<String>();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.choose);
        String[] linesToShow = getLinesToShow(lines);
        alertDialogBuilder.setMultiChoiceItems(linesToShow, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                ListView lw = ((AlertDialog) dialog).getListView();
                String lineName = (String) lw.getItemAtPosition(which);
                if (isChecked) {
                    linesToAdd.add(lineName);
                }
                else if (linesToAdd.contains(lineName)) {
                    linesToAdd.remove(lineName);
                }
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                List<Line> lines = addMatchingLines(linesToAdd, clickedStop);
                List<TransportationType> types = getTransportationTypesFromLines(lines, clickedStop);
                if (types.size() > 0) {
                    onListItemsSelected(types);
                }
                finish();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }



    private List<Line> addMatchingLines(List<String> linesToAdd, Stop clickedStop) {
        List <Line> lines = new ArrayList<Line>();
        for (String lineToAdd : linesToAdd) {
            for (Line line : clickedStop.getLines()) {
                if (lineToAdd.equalsIgnoreCase(line.getName())) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private String[] getLinesToShow(List<String> linesList) {
        String [] lines = new String[linesList.size()];
        for (int i =0; i<lines.length; i++) {
            lines[i] = linesList.get(i);
        }

        return lines;
    }

    private List<TransportationType> getTransportationTypesFromLines(List<Line> lines, Stop clickedStop) {
        List <TransportationType> types = new ArrayList<TransportationType>();
        boolean typeExists = false;
        boolean stopExists = false;
        for (Line line : lines) {
            for (TransportationType type : types) {
                if (type.getType() == line.getTransportationType()) {
                    typeExists = true;
                    for (Stop stop : type.getStops()) {
                        if (stop.getId() == clickedStop.getId()) {
                            stopExists = true;
                            stop.addLine(line);
                        }
                    }
                    if (!stopExists) {
                        Stop stop = new Stop(clickedStop.getId(), clickedStop.getName(), clickedStop.getDistrict(), clickedStop.getLines());
                        stop.setLines(new ArrayList<Line>(asList(line)));
                        type.addStop(stop);
                    }
                }
            }
            if (!typeExists) {
                Stop stop = new Stop(clickedStop.getId(), clickedStop.getName(), clickedStop.getDistrict(), clickedStop.getLines());
                stop.setLines(new ArrayList<Line>(asList(line)));
                TransportationType type = new TransportationType(asList(stop), line.getTransportationType());
                types.add(type);
            }
        }

        return types;
    }

}
