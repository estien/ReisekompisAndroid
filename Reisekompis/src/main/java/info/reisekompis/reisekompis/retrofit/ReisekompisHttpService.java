package info.reisekompis.reisekompis.retrofit;

import info.reisekompis.reisekompis.Stop;
import info.reisekompis.reisekompis.configuration.ReisekompisService;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface ReisekompisHttpService {
    @GET(ReisekompisService.SEARCH + "/{query}")
    void searchForStops(@Path("query") String stopQuery, Callback<Stop[]> cb);
}



