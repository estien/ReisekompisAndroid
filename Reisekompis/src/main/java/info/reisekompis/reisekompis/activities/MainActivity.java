package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import info.reisekompis.reisekompis.Departure;
import info.reisekompis.reisekompis.DepartureAdapter;
import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.SimpleStop;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopsAdapter;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.configuration.ReisekompisService;
import info.reisekompis.reisekompis.fragments.FindStopsFragment;
import info.reisekompis.reisekompis.fragments.ListDeparturesFragment;

import static info.reisekompis.reisekompis.SimpleStop.simpleStopsFromStops;
import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;
import static java.util.Arrays.asList;


public class MainActivity extends Activity implements OnListItemSelectedListener  {


    HttpClient httpClient;
    SharedPreferences sharedPreferences;
    private View noDeparturesSelectedView;
    private View progressBarLoading;


    private SharedPreferences.Editor editor;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new HttpClient();

        sharedPreferences = this.getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String s = sharedPreferences.getString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
        if (s == null) {
            // force search field or first time info popup
        }

        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        handleIntent(getIntent());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new ListDeparturesFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshDepartures();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null) {
                query = query.trim();
                if(query.length() < 4) return; // TODO: add dialog informing about minimum search length
                new SearchStopsAsyncTask().execute(ReisekompisService.SEARCH + query);
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
            progressBarLoading.setVisibility(View.VISIBLE);
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
            progressBarLoading.setVisibility(View.INVISIBLE);
            FindStopsFragment fragment = (FindStopsFragment) getFragmentManager().findFragmentById(R.id.main_fragment_container);
            StopsAdapter adapter = new StopsAdapter(MainActivity.this, R.layout.stop_list_item, result);
            fragment.setListAdapter(adapter);
        }
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
