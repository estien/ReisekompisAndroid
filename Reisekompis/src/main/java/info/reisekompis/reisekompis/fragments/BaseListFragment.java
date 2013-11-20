package info.reisekompis.reisekompis.fragments;

import android.app.ListFragment;
import android.content.SharedPreferences;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.activities.MainActivity;

public class BaseListFragment extends ListFragment {

    public SharedPreferences getSharedPreferences() {
        return ((MainActivity)getActivity()).getSharedPreferences();
    }

    public HttpClient getHttpClient() {
        return ((MainActivity) getActivity()).getHttpClient();
    }
}
