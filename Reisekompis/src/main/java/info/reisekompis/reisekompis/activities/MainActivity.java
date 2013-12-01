package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.fragments.FindStopsFragment;
import info.reisekompis.reisekompis.fragments.ListDeparturesFragment;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;


public class MainActivity extends Activity implements OnListItemSelectedListener  {


    HttpClient httpClient;
    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    boolean performingSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpClient = new HttpClient();
        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        String s = sharedPreferences.getString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
        if (s == null) {
            // force search field or first time info popup
        }
        else if (!isSearching()){
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new ListDeparturesFragment())
                    .commit();
        }

        handleSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshDepartures();
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

                Bundle args = new Bundle();
                args.putString("query", query);
                FindStopsFragment findStopsFragment = new FindStopsFragment();
                findStopsFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, findStopsFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
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


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public HttpClient getHttpClient() {
        return httpClient;
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


    private List<TransportationType> getDummyData() {
        List<TransportationType> list = new ArrayList<TransportationType>();

        ArrayList<Stop> stops1 = new ArrayList<Stop>();
        ArrayList<Line> lines1 = new ArrayList<Line>();
        lines1.add(new Line(1, "Line ", TransportationType.Type.METRO));
        lines1.add(new Line(2, "Line ", TransportationType.Type.METRO));
        stops1.add(new Stop(3010610, "Grønland", "Oslo", lines1));
        list.add(new TransportationType(stops1, TransportationType.Type.METRO));

        ArrayList<Stop> stops2 = new ArrayList<Stop>();
        ArrayList<Line> lines2 = new ArrayList<Line>();
        lines2.add(new Line(8401, "Line", TransportationType.Type.BUS));
        stops2.add(new Stop(6041460, "Et eller annet", "Oslo", lines2));
        list.add(new TransportationType(stops2, TransportationType.Type.BUS));

        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh :
                //refreshDepartures();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
