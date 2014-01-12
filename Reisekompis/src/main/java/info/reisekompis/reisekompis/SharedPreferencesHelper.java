package info.reisekompis.reisekompis;

import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import info.reisekompis.reisekompis.configuration.Configuration;

public class SharedPreferencesHelper {
    public TransportationType[] getStoredTransportationTypes(SharedPreferences sharedPreferences, ObjectMapper mapper) {
        try {
            String value = sharedPreferences.getString(Configuration.SHARED_PREFERENCES_TRANSPORTATION_TYPES, null);
            if (value != null) {
                TransportationType[] transportationTypes = mapper.readValue(value, TransportationType[].class);
                return transportationTypes != null ? transportationTypes : new TransportationType[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TransportationType[0];
    }
}
