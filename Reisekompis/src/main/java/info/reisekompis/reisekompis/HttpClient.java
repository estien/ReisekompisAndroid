package info.reisekompis.reisekompis;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                if (data != null) {
                    data.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String post(String url, Object params) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        String result = "";
        InputStream data = null;
        BufferedReader reader = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            HttpPost method = new HttpPost(new URI(url));
            String paramsAsJson = objectMapper.writeValueAsString(params);
            StringEntity entity = new StringEntity(paramsAsJson);
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            method.setEntity(entity);
            HttpResponse response = defaultHttpClient.execute(method);
            data = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(data, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        finally {
            try {
                if (data != null) {
                    data.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
