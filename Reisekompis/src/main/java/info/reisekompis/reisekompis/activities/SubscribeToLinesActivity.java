package info.reisekompis.reisekompis.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.SharedPreferencesHelper;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopsAdapter;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.retrofit.ReisekompisHttpService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static info.reisekompis.reisekompis.configuration.Configuration.RETROFIT_SERVICE_BASE_PATH;
import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;
import static java.util.Arrays.asList;

public class SubscribeToLinesActivity extends ListActivity implements OnListItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private SharedPreferencesHelper sharedPreferencesHelper;
    MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe);
        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        sharedPreferencesHelper = new SharedPreferencesHelper();
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

        TransportationType[] existingTransportationTypes = sharedPreferencesHelper.getStoredTransportationTypes(sharedPreferences, mapper);
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

    private List<TransportationType> mergeExistingWithSelectedTranportationTypes(TransportationType[] existingTransportationTypes, List<TransportationType> selectedLines) {
        List<TransportationType> mergedLines = new ArrayList<TransportationType>();
        mergedLines.addAll(selectedLines);
        Collections.addAll(mergedLines, existingTransportationTypes);

        return mergedLines;
    }

    private void handleSearch() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(RETROFIT_SERVICE_BASE_PATH)
                .build();
        ReisekompisHttpService reisekompisHttpService = restAdapter.create(ReisekompisHttpService.class);
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        if (query != null) {
            query = query.trim();
            if(query.length() < 4) return;
            setProgressBarVisible(true);
            reisekompisHttpService.searchForStops(query, new Callback<Stop[]>() {
                @Override
                public void success(Stop[] stops, Response response) {
                    setProgressBarVisible(false);
                    searchMenuItem.collapseActionView();
                    StopsAdapter adapter = new StopsAdapter(getApplicationContext(),
                            R.layout.stop_list_item, stops);
                    setListAdapter(adapter);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    setProgressBarVisible(false);
                    Log.e("Error in stopSearch", retrofitError.getMessage());
                }
            });
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
