package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import info.reisekompis.reisekompis.HttpClient;
import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.SimpleStop;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.configuration.ReisekompisService;

import static info.reisekompis.reisekompis.SimpleStop.simpleStopsFromStops;


public class MainActivity extends Activity {

    HttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new HttpClient();

        SharedPreferences sharedPreferences = this.getSharedPreferences(Configuration.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        List<TransportationType> lines = getDummyData();

        new PollAsyncTask().execute(lines);

//        startActivity(new Intent(this, FindStopsActivity.class));
    }

    private class PollAsyncTask extends AsyncTask<List<TransportationType>, Void, Void> {

        @Override
        protected Void doInBackground(List<TransportationType>... params) {
            List<SimpleStop> simpleStops = new ArrayList<SimpleStop>();
            for (TransportationType t : params[0]) {
                simpleStops.addAll(simpleStopsFromStops(t.getStops()));
            }

            httpClient.post(ReisekompisService.POLL, simpleStops);
            return null;
        }
    }

    private List<TransportationType> getDummyData() {
        List<TransportationType> list = new ArrayList<TransportationType>();

        for (int i = 1; i <= 2; i++) {

            List<Stop> stops = new ArrayList<Stop>();
            for (int j = 1; j <= 2; j++) {

                List<Line> lines = new ArrayList<Line>();
                for (int k = 1; k <= 3; k++) {
                    lines.add(new Line(k, "Line " + k, TransportationType.Type.values()[i]));
                }

                stops.add(new Stop(j, "Stop " + j, "District " + j, lines));
            }
            list.add(new TransportationType(stops, TransportationType.Type.values()[i]));
        }
        return list;
    }
}
