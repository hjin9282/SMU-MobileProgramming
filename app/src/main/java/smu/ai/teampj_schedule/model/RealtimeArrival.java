package smu.ai.teampj_schedule.model;

import com.google.gson.annotations.SerializedName;

public class RealtimeArrival {
    // 상행(0), 하행(1) 구분
    @SerializedName("updnLine")
    public String updnLine;

    // 도착지 방면 (예: 당고개행, 사당행)
    @SerializedName("trainLineNm")
    public String trainLineNm;

    // 첫 번째 도착 메시지 (예: 전역 도착, 3분 후)
    @SerializedName("arvlMsg2")
    public String arvlMsg2;

    // 현재 위치 (예: 서울역) -> 이걸로 열차 아이콘 위치 잡을 거야
    @SerializedName("arvlMsg3")
    public String currentLocation;

    // 열차가 몇 초 후에 도착하는지 (문자열)
    @SerializedName("barvlDt")
    public String barvlDt;

    // 도착 상태 코드
    @SerializedName("arvlCd")
    public String arvlCd;
}