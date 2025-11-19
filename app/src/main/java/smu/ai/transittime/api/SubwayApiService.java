package smu.ai.transittime.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import smu.ai.transittime.model.StationResponse;

public interface SubwayApiService {

    @GET("{KEY}/json/SearchSTNBySubwayLineInfo/1/1000/")
    Call<StationResponse> getStations(@Path("KEY") String key);
}
