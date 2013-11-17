package info.reisekompis.reisekompis;

import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;

import org.joda.time.DateTime;

public class CustomDateTimeDeserializer extends DateTimeDeserializer {

    public CustomDateTimeDeserializer() {
        super(DateTime.class);
    }

}
