package smu.ai.teampj_schedule;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "subway";
    private static final String KEY_LINE = "line";
    private static final String KEY_STATION = "station";

    // 저장된 호선 가져오기 (저장된 게 없으면 기본값 반환)
    public static String getLine(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LINE, "1호선"); // 기본값: 1호선
    }

    // 저장된 역 이름 가져오기
    public static String getStation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_STATION, "서울역"); // 기본값: 서울역
    }
}