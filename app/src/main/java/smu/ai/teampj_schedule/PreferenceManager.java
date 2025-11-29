package smu.ai.teampj_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;

public class PreferenceManager {
    // 저장소 이름 ("subgo")
    private static final String PREF_NAME = "subgo";

    // 키 값 ("station_info")
    private static final String KEY_INFO = "station_info";

    public static void saveStationInfo(Context context,
                                       String stationName,
                                       String lineNumber,
                                       String stationCode) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("station_nm", stationName);
            obj.put("line_num", lineNumber);
            obj.put("station_cd", stationCode);

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_INFO, obj.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 저장된 역 이름 꺼내기 (예: "숙대입구역")
    public static String getStation(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "서울역";

            JSONObject jsonObject = new JSONObject(jsonString);
            String name = jsonObject.getString("station_nm");

            // 이미 '역'으로 끝나지 않으면 붙여주기
            if (!name.endsWith("역")) {
                name = name + "역";
            }

            return name;

        } catch (Exception e) {
            e.printStackTrace();
            return "서울역";
        }
    }


    // 저장된 호선 꺼내기 (예: "4호선")
    public static String getLine(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "1호선";

            JSONObject obj = new JSONObject(jsonString);
            String raw = obj.getString("line_num");

            // "호선" 제거
            String num = raw.replace("호선", "");

            // 숫자로 변환 (예외 방지)
            int n = Integer.parseInt(num);

            return n + "호선";

        } catch (Exception e) {
            e.printStackTrace();
            return "1호선";
        }
    }


    public static String getStationCode(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "";

            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("station_cd");

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String normalizeStationName(String input) {
        if (input == null) return "";
        input = input.trim();

        if (input.endsWith("역")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

}