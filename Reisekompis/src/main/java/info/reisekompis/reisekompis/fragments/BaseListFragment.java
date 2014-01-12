package info.reisekompis.reisekompis.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ProgressBar;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.activities.BaseActivity;
import info.reisekompis.reisekompis.activities.MainActivity;

public class BaseListFragment extends ListFragment {

    protected BaseActivity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (BaseActivity) activity;
        super.onAttach(activity);
    }

    public SharedPreferences getSharedPreferences() {
        return activity.getSharedPreferences();
    }

    public HttpClient getHttpClient() {
        return activity.getHttpClient();
    }

    public void setProgressBarVisible(boolean visible) {
        ProgressBar progressBar = activity.getProgressBar();
        if (progressBar != null) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
