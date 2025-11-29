package smu.ai.teampj_schedule.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import smu.ai.teampj_schedule.model.StationResponse;
import smu.ai.teampj_schedule.model.RealtimeResponse;
import smu.ai.teampj_schedule.model.TimeTableResponse;

public interface SubwayApiService {
    // 역 목록 API
    @GET("{KEY}/json/SearchSTNBySubwayLineInfo/1/1000/")
    Call<StationResponse> getStations(@Path("KEY") String key);

    // 실시간 도착 정보 API
    @GET
    Call<RealtimeResponse> getRealtimeArrivals(@Url String fullUrl);

    // 역코드로 지하철 열차 시간표 검색 API
    @GET("{KEY}/json/SearchSTNTimeTableByIDService/{START}/{END}/{STATION_CD}/{WEEK}/{INOUT}")
    Call<TimeTableResponse> getTimeTable(
            @Path("KEY") String key,
            @Path("START") int start,
            @Path("END") int end,
            @Path("STATION_CD") String stationCode,
            @Path("WEEK") String weekTag,
            @Path("INOUT") String inoutTag
    );

}
