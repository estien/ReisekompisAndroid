package info.reisekompis.reisekompis;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import info.reisekompis.reisekompis.configuration.ReisekompisService;

public class FindStopsActivity extends Activity {
    private HttpClient httpClient;
    private StopListFragment listeFragment;
    private Stop[] transportationStops;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_stops_activity);
        httpClient = new HttpClient();

        handleIntent(getIntent());

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            listeFragment = new StopListFragment();
            fragmentTransaction.replace(R.id.list_container, listeFragment).commit();
            fragmentManager.executePendingTransactions();
        }
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
                if(query.length() < 4) return;
                new SearchStopsAsyncTask().execute(ReisekompisService.SEARCH + query);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SearchStopsAsyncTask extends AsyncTask<String, Void, Stop[]> {
        @Override
        protected Stop[] doInBackground(String... params) {
            String jsonResponseString = httpClient.get(params[0]);
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(jsonResponseString, Stop[].class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Stop[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Stop[] result) {
            if(result == null) return;
            ArrayAdapter<Stop> arrayAdapter = new ArrayAdapter<Stop>(FindStopsActivity.this, android.R.layout.simple_list_item_1);
            transportationStops = result;
            arrayAdapter.addAll(transportationStops);
            listeFragment.setListAdapter(arrayAdapter);
        }
    }
}
