package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import info.reisekompis.reisekompis.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, FindStopsActivity.class));
    }
}
