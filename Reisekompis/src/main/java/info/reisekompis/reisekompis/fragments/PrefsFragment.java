package info.reisekompis.reisekompis.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.SharedPreferencesHelper;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_LINE_PREFERENCE;

public class PrefsFragment extends PreferenceFragment{
    public static final String Tag = "prefs_fragment";

    private SharedPreferences sharedPreferences;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, getActivity().MODE_PRIVATE);
        sharedPreferencesHelper = new SharedPreferencesHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        TransportationType[] existingTransportationTypes = sharedPreferencesHelper.getStoredTransportationTypes(sharedPreferences, new ObjectMapper());
        populateExistingPreferences(existingTransportationTypes);

    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }

    private int getNumberOfLines(TransportationType[] types) {
        int numLines = 0;
        for (TransportationType type : types) {
            List<Stop> stops = type.getStops();
            for (Stop stop : stops) {
                numLines += stop.getLines().size();
            }
        }
        return numLines;
    }

    private void populateExistingPreferences(TransportationType[] types) {
        int numLines = getNumberOfLines(types);
        MultiSelectListPreference linePreference = (MultiSelectListPreference) findPreference(SHARED_PREFERENCES_LINE_PREFERENCE);
        CharSequence[] entries = new CharSequence[numLines];
        CharSequence[] entryValues = new CharSequence[numLines];
        int i = 0;
        for (TransportationType type : types) {
            List<Stop> stops = type.getStops();
            for (Stop stop : stops) {
                String stopName = stop.getName();
                ArrayList<Line> lines = stop.getLines();
                for (Line line : lines) {
                    entries[i] = line.getName() + ", " + stopName;
                    entryValues[i] = line.getName() + ", " + stopName;
                    i++;
                }
            }
        }
        linePreference.setEntries(entries);
        linePreference.setEntryValues(entryValues);
    }
}
