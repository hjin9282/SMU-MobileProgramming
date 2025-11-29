package smu.ai.teampj_schedule.model;

import com.google.gson.annotations.SerializedName;

public class StationRow {
    @SerializedName("LINE_NUM")
    public String lineNumber;

    @SerializedName("STATION_NM")
    public String stationName;

    @SerializedName("STATION_CD")
    public String stationCode;

}