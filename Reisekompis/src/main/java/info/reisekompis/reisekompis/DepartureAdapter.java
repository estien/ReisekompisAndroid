package info.reisekompis.reisekompis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DepartureAdapter extends ArrayAdapter {

    private final LayoutInflater inflater;

    public DepartureAdapter(Context context, int resource, Departure[] objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : inflater.inflate(R.layout.departure_list_item, null);
        Departure departure = (Departure) getItem(position);

        TextView lineHeading = (TextView) view.findViewById(R.id.departure_line_name);
        lineHeading.setText(Integer.toString(departure.getLineId()));

        TextView departureText = (TextView) view.findViewById(R.id.depature_destination);
        departureText.setText(departure.getDestination());

        TextView time = (TextView) view.findViewById(R.id.departure_time);
        time.setText(departure.getTime().toLocalDateTime().toString());

        return view;
    }
}
