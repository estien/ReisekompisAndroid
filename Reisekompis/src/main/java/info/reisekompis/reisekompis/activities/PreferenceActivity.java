package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.os.Bundle;

import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.fragments.PrefsFragment;

public class PreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.preferences);
        super.onCreate(savedInstanceState);

        PrefsFragment prefsFragment = PrefsFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_fragment_container, prefsFragment, PrefsFragment.Tag)
                .commit();
    }
}
