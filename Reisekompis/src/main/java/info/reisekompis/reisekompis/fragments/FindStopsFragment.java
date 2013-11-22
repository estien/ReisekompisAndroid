package info.reisekompis.reisekompis.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.reisekompis.reisekompis.Line;
import info.reisekompis.reisekompis.R;
import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.StopsAdapter;
import info.reisekompis.reisekompis.TransportationType;
import info.reisekompis.reisekompis.activities.OnListItemSelectedListener;
import info.reisekompis.reisekompis.configuration.Configuration;
import info.reisekompis.reisekompis.configuration.ReisekompisService;

import static java.util.Arrays.asList;

public class FindStopsFragment extends BaseListFragment {
    OnListItemSelectedListener listener;
    private String query;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnListItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnListItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String searchQuery = ReisekompisService.SEARCH + getArguments().getString("query");
        new SearchStopsAsyncTask().execute(searchQuery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_stops, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Stop clickedStop = (Stop) getListAdapter().getItem(position);
        List<String> lines = new ArrayList<String>();
        for (Line line : clickedStop.getLines()) {
            lines.add(line.getName());
        }
        final List<String> linesToAdd = new ArrayList<String>();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(R.string.choose);
        String[] linesToShow = getLinesToShow(lines);
        alertDialogBuilder.setMultiChoiceItems(linesToShow, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                ListView lw = ((AlertDialog) dialog).getListView();
                String lineName = (String) lw.getItemAtPosition(which);
                if (isChecked) {
                    linesToAdd.add(lineName);
                }
                else if (linesToAdd.contains(lineName)) {
                    linesToAdd.remove(lineName);
                }
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                List<Line> lines = addMatchingLines(linesToAdd, clickedStop);
                List<TransportationType> types = getTransportationTypesFromLines(lines, clickedStop);
                if (types.size() > 0) {
                    listener.onListItemsSelected(types);
                }
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private List<Line> addMatchingLines(List<String> linesToAdd, Stop clickedStop) {
        List <Line> lines = new ArrayList<Line>();
        for (String lineToAdd : linesToAdd) {
            for (Line line : clickedStop.getLines()) {
                if (lineToAdd.equalsIgnoreCase(line.getName())) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private String[] getLinesToShow(List<String> linesList) {
        String [] lines = new String[linesList.size()];
        for (int i =0; i<lines.length; i++) {
            lines[i] = linesList.get(i);
        }

        return lines;
    }

    private List<TransportationType> getTransportationTypesFromLines(List<Line> lines, Stop clickedStop) {
        List <TransportationType> types = new ArrayList<TransportationType>();
        boolean typeExists = false;
        boolean stopExists = false;
        for (Line line : lines) {
            for (TransportationType type : types) {
                if (type.getType() == line.getTransportationType()) {
                    typeExists = true;
                    for (Stop stop : type.getStops()) {
                        if (stop.getId() == clickedStop.getId()) {
                            stopExists = true;
                            stop.addLine(line);
                        }
                    }
                    if (!stopExists) {
                        Stop stop = new Stop(clickedStop.getId(), clickedStop.getName(), clickedStop.getDistrict(), clickedStop.getLines());
                        stop.setLines(new ArrayList<Line>(asList(line)));
                        type.addStop(stop);
                    }
                }
            }
            if (!typeExists) {
                Stop stop = new Stop(clickedStop.getId(), clickedStop.getName(), clickedStop.getDistrict(), clickedStop.getLines());
                stop.setLines(new ArrayList<Line>(asList(line)));
                TransportationType type = new TransportationType(asList(stop), line.getTransportationType());
                types.add(type);
            }
        }

        return types;
    }

    class SearchStopsAsyncTask extends AsyncTask<String, Void, Stop[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBarLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Stop[] doInBackground(String... params) {
            String jsonResponseString = getHttpClient().get(params[0]);
            ObjectMapper mapper = new ObjectMapper();
            Stop[] result;
            try {
                result = mapper.readValue(jsonResponseString, Stop[].class);
            } catch (IOException e) {
                e.printStackTrace();
                return new Stop[0];
            }
            return result;
        }

        @Override
        protected void onPostExecute(Stop[] result) {
            //progressBarLoading.setVisibility(View.INVISIBLE);
            StopsAdapter adapter = new StopsAdapter(activity, R.layout.stop_list_item, result);
            FindStopsFragment.this.setListAdapter(adapter);
        }
    }

}
