package info.reisekompis.reisekompis;

public enum TransportationType {
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

    TransportationType(int transportationId) {
        this.transportationId = transportationId;
    }

    public int getTransportationId() {
        return transportationId;
    }
}
