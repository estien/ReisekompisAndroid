package info.reisekompis.reisekompis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.joda.time.DateTime;

public class Departure {
    private int stopId;
    @JsonProperty(value = "id")
    private int lineId;
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime time;
    private String destination;

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getStopId() {
        return stopId;
    }

    public int getLineId() {
        return lineId;
    }

    public DateTime getTime() {
        return time;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Departure{" +
                "stopId=" + stopId +
                ", lineId=" + lineId +
                ", time=" + time +
                ", destination='" + destination + '\'' +
                '}';
    }
}
