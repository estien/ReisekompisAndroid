package info.reisekompis.reisekompis.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.activities.MainActivity;

public class BaseListFragment extends ListFragment {

    protected MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
    }

    public SharedPreferences getSharedPreferences() {
        return activity.getSharedPreferences();
    }

    public HttpClient getHttpClient() {
        return activity.getHttpClient();
    }
}
