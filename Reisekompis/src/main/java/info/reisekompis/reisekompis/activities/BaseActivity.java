package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.configuration.Configuration;

public class BaseActivity extends Activity {

    protected HttpClient httpClient;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = new HttpClient();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        sharedPreferences = getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
