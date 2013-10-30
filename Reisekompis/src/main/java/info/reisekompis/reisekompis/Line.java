package info.reisekompis.reisekompis;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Line {
    private int id;
    private String name;
    @JsonProperty(value="trans")
    private TransportationType transportationType;

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

    public TransportationType getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType transportationType) {
        this.transportationType = transportationType;
    }

}
