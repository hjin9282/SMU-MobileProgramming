package smu.ai.teampj_schedule;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StationUtils {

    private static JSONArray stationArray;

    private static void loadJson(Context context) {
        if (stationArray != null) return;

        try {
            InputStream is = context.getAssets().open("stations.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            stationArray = new JSONArray(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String findStationCode(Context context, String line, String stationName) {
        loadJson(context);

        // "역" 제거
        stationName = stationName.replace("역", "");

        try {
            for (int i = 0; i < stationArray.length(); i++) {
                JSONObject obj = stationArray.getJSONObject(i);

                String lineNum = obj.getString("line");
                String name = obj.getString("station_nm");

                // 라인 + 역 이름 매칭
                if (lineNum.equals(line) && name.equals(stationName)) {
                    return obj.getString("station_cd");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
