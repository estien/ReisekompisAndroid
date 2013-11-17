package info.reisekompis.reisekompis.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.reisekompis.reisekompis.Departure;
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

    private class PollAsyncTask extends AsyncTask<List<TransportationType>, Void, Departure[]> {

        @Override
        protected Departure[] doInBackground(List<TransportationType>... params) {
            List<SimpleStop> simpleStops = new ArrayList<SimpleStop>();
            for (TransportationType t : params[0]) {
                simpleStops.addAll(simpleStopsFromStops(t.getStops()));
            }

            String jsonResponseString = httpClient.post(ReisekompisService.POLL, simpleStops);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            Departure[] result;
            try {
                result = mapper.readValue(jsonResponseString, Departure[].class);
            } catch (IOException e) {
                e.printStackTrace();
                return new Departure[0];
            }
            return result;
        }
    }

    private List<TransportationType> getDummyData() {
        List<TransportationType> list = new ArrayList<TransportationType>();

        ArrayList<Stop> stops1 = new ArrayList<Stop>();
        ArrayList<Line> lines1 = new ArrayList<Line>();
        lines1.add(new Line(1, "Line ", TransportationType.Type.METRO));
        lines1.add(new Line(2, "Line ", TransportationType.Type.METRO));
        stops1.add(new Stop(3010610, "Grønland", "Oslo", lines1));
        list.add(new TransportationType(stops1, TransportationType.Type.METRO));

        ArrayList<Stop> stops2 = new ArrayList<Stop>();
        ArrayList<Line> lines2 = new ArrayList<Line>();
        lines2.add(new Line(8401, "Line", TransportationType.Type.BUS));
        stops2.add(new Stop(6041460, "Et eller annet", "Oslo", lines2));
        list.add(new TransportationType(stops2, TransportationType.Type.BUS));

        return list;
    }
}
