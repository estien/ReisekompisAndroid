package info.reisekompis.reisekompis;

import java.util.List;

public class TransportationType {

    enum Type {
        WALKING(0),
        AIRPORT_BUS(1),
        BUS(2),
        DUMMY_VALUE(3),
        AIRPORT_TRAIN(4),
        BOAT(5),
        TRAIN(6),
        TRAM(7),
        METRO(8);

        private int transportationId;

        Type(int transportationId) {
            this.transportationId = transportationId;
        }

        public int getTransportationId() {
            return transportationId;
        }
    }

    List<Stop> stops;
    Type type;

    public TransportationType(List<Stop> stops, Type type) {
        this.stops = stops;
        this.type = type;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public Type getType() {
        return type;
    }
}
