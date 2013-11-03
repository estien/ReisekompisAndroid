package info.reisekompis.reisekompis.activities;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopListFragment;
import info.reisekompis.reisekompis.StopsArrayAdapter;
import info.reisekompis.reisekompis.configuration.ReisekompisService;

public class FindStopsActivity extends Activity {
    private HttpClient httpClient;
    private StopListFragment listeFragment;
    private Stop[] transportationStops;
    private ProgressBar searchingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_stops_activity);
        httpClient = new HttpClient();
        searchingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_searching);

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
    protected void onResume() {
        super.onResume();
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
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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
            searchingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Stop[] result) {
            searchingProgressBar.setVisibility(View.INVISIBLE);
            if (result == null) return;
            transportationStops = result;
            StopsArrayAdapter stopsArrayAdapter = new StopsArrayAdapter(FindStopsActivity.this, R.layout.stop_list_item, result);
            listeFragment.setListAdapter(stopsArrayAdapter);
        }
    }
}
