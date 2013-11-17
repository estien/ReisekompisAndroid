package info.reisekompis.reisekompis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleStop {

    @JsonProperty
    private int id;
    @JsonProperty
    private List<Integer> lines;

    public SimpleStop(int id, List<Integer> lines) {
        this.id = id;
        this.lines = lines;
    }

    public static SimpleStop simpleStopFromStop(Stop stop) {
        List<Integer> linenumbers = new ArrayList<Integer>();
        List<Line> lines = stop.getLines();
        for (Line l : lines) {
            linenumbers.add(l.getId());
        }
        return new SimpleStop(stop.getId(), linenumbers);
    }

    public static List<SimpleStop> simpleStopsFromStops(List<Stop> stops) {
        List<SimpleStop> simpleStops = new ArrayList<SimpleStop>();
        for (Stop stop : stops) {
            simpleStops.add(simpleStopFromStop(stop));
        }
        return simpleStops;
    }
}
