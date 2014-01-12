package info.reisekompis.reisekompis.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.fragments.FindStopsFragment;
import info.reisekompis.reisekompis.fragments.PrefsFragment;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;

public class PreferenceActivity extends BaseActivity implements OnListItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.preferences);
        super.onCreate(savedInstanceState);

        PrefsFragment prefsFragment = PrefsFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_fragment_container, prefsFragment, PrefsFragment.Tag)
                .commit();

        handleSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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

        editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, json);
        editor.commit();
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
                FindStopsFragment findStopsFragment = FindStopsFragment.newInstance(query);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.preferences_fragment_container, findStopsFragment, FindStopsFragment.Tag)
                        .commit();
            }
        }
    }

}
