package info.reisekompis.reisekompis;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class HttpClient {
    public String get(String url) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        String result = "";
        InputStream data = null;
        BufferedReader reader = null;
        try {
            HttpGet method = new HttpGet(new URI(url));
            HttpResponse response = defaultHttpClient.execute(method);
            data = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(data, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                data.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
