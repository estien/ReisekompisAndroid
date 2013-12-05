package info.reisekompis.reisekompis.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.reisekompis.reisekompis.Departure;
import info.reisekompis.reisekompis.DepartureAdapter;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.SimpleStop;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.configuration.ReisekompisService;

import static info.reisekompis.reisekompis.SimpleStop.simpleStopsFromStops;
import static info.reisekompis.reisekompis.configuration.Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES;

public class ListDeparturesFragment extends BaseListFragment {

    public static final String TAG = "fragment_list_departures";
    private TransportationType[] transportationTypes;
    private View lastUpdatedContainer;

    private TextView lastUpdatedTime;
    private View noDeparturesSelectedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_departures, container, false);

        lastUpdatedContainer = view.findViewById(R.id.last_updated_container);
        noDeparturesSelectedView = view.findViewById(R.id.no_departures_selected);
        lastUpdatedTime = (TextView) view.findViewById(R.id.last_updated_time);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
       refreshDepartures();
    }

    public void refreshDepartures() {
        String s = getSharedPreferences().getString(SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
        if (s == null) return;

        noDeparturesSelectedView.setVisibility(View.INVISIBLE);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            transportationTypes = objectMapper.readValue(s, TransportationType[].class);
            new PollAsyncTask().execute(Arrays.asList(transportationTypes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PollAsyncTask extends AsyncTask<List<TransportationType>, Void, Departure[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarVisible(true);
        }

        @Override
        protected Departure[] doInBackground(List<TransportationType>... params) {
            List<SimpleStop> simpleStops = new ArrayList<SimpleStop>();
            for (TransportationType t : params[0]) {
                simpleStops.addAll(simpleStopsFromStops(t.getStops()));
            }

            String jsonResponseString = getHttpClient().post(ReisekompisService.POLL, simpleStops);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            Departure[] result;
            try {
                result = mapper.readValue(jsonResponseString, Departure[].class);
            } catch (IOException e) {
                e.printStackTrace();
                return new Departure[0];
            }

            boolean stopNameFoundForDeparture = false;
            for(Departure departure : result) {
                int stopId  = departure.getStopId();
                for(TransportationType type : transportationTypes) {
                    if(stopNameFoundForDeparture) break;
                    for(Stop stop : type.getStops()) {
                        if(stop.getId() == stopId) {
                            departure.setStopName(stop.getName());
                            stopNameFoundForDeparture = true;
                            break;
                        }
                    }
                }
                stopNameFoundForDeparture = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Departure[] departures) {
            Log.d(getClass().getName(), Arrays.toString(departures));
            Activity activity1 = getActivity();
            if(activity1 == null) return; // is detached. temp
            DepartureAdapter adapter = new DepartureAdapter(activity1, R.id.departure_line_name, departures);
            setProgressBarVisible(false);
            boolean anyDepartures = departures.length > 0;
            noDeparturesSelectedView.setVisibility(anyDepartures ? View.INVISIBLE : View.VISIBLE);

            DateTime now = new DateTime(DateTimeZone.getDefault());
            lastUpdatedTime.setText(now.toString("HH:mm:ss"));

            if (anyDepartures) {
                lastUpdatedContainer.setVisibility(View.VISIBLE);
            }

            setListAdapter(adapter);
        }
    }
}
