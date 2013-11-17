package info.reisekompis.reisekompis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Line {
    private int id;
    private String name;
    @JsonProperty(value="type")
    private TransportationType.Type transportationType;

    @JsonCreator
    public Line() {
    }

    public Line(int id, String name, TransportationType.Type transportationType) {
        this.id = id;
        this.name = name;
        this.transportationType = transportationType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransportationType.Type getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType.Type transportationType) {
        this.transportationType = transportationType;
    }

    public String toString() {
        return name;
    }

}
