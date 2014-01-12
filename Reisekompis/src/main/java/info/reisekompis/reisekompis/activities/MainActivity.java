package info.reisekompis.reisekompis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.fragments.ListDeparturesFragment;

import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        String s = sharedPreferences.getString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
        if (s == null) {
            // force search field or first time info popup
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new ListDeparturesFragment(), ListDeparturesFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh :
                ListDeparturesFragment listDeparturesFragment = (ListDeparturesFragment) getFragmentManager().findFragmentByTag(ListDeparturesFragment.TAG);
                if(listDeparturesFragment != null) {
                    listDeparturesFragment.refreshDepartures();
                }

                return true;
            case R.id.settings :
                Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
                startActivity(preferenceIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
