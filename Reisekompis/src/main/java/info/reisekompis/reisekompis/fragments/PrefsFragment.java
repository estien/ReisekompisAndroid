package info.reisekompis.reisekompis.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import info.reisekompis.reisekompis.R;

public class PrefsFragment extends PreferenceFragment{
    public static final String Tag = "prefs_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }
}
