package info.reisekompis.reisekompis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopsArrayAdapter extends ArrayAdapter {

    private final LayoutInflater inflater;

    public StopsArrayAdapter(Context context, int resource, Stop[] objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : inflater.inflate(R.layout.stop_list_item, null);
        Stop stop = (Stop) getItem(position);

        TextView heading = (TextView) view.findViewById(R.id.stop_name_text);
        heading.setText(stop.getName());

        TextView district = (TextView) view.findViewById(R.id.stop_district_text);
        district.setText(stop.getDistrict());

        TextView busLines  = (TextView) view.findViewById(R.id.bus_lines_text);
        StringBuilder builder = new StringBuilder();
        for(Line line : stop.getLines()){
            builder.append(line.getName() + ", ");
        }
        busLines.setText(builder.toString());

        return view;
    }
}
