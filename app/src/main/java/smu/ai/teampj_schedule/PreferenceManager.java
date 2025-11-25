package smu.ai.teampj_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;

public class PreferenceManager {
    // 저장소 이름 ("subgo")
    private static final String PREF_NAME = "subgo";

    // 키 값 ("station_info")
    private static final String KEY_INFO = "station_info";

    // 저장된 역 이름 꺼내기 (예: "숙대입구")
    public static String getStation(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "서울역"; // 저장된 게 없으면 기본값

            // JSON 껍질 까기
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("station_nm"); // 팀원 코드에 있는 키 이름

        } catch (Exception e) {
            e.printStackTrace();
            return "서울역"; // 에러 나면 기본값
        }
    }

    // 저장된 호선 꺼내기 (예: "04호선")
    public static String getLine(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "01호선"; // 기본값

            // JSON 데이터 꺼내오기
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("line_num"); // 팀원 코드에 있는 키 이름

        } catch (Exception e) {
            e.printStackTrace();
            return "01호선"; // 에러 나면 기본값
        }
    }
}