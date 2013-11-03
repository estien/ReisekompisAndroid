package info.reisekompis.reisekompis;

public class StringHelper {

    public static String Join(String separator, Object[] objects) {
        if(objects == null || separator == null || objects.length == 0) return "";

        StringBuilder builder = new StringBuilder();
        int lastObjectIndex = objects.length - 1;
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            builder.append(obj.toString() + (lastObjectIndex == i ? "" : separator + " ") );
        }
        return builder.toString();
    }
}
