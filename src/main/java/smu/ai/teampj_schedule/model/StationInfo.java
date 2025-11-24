package smu.ai.teampj_schedule.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StationInfo {

    @SerializedName("row")
    public List<StationRow> rows;
}