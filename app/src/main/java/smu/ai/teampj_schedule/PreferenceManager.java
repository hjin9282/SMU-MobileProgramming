package smu.ai.teampj_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private static final String PREF_NAME = "subgo";
    private static final String KEY_INFO = "station_info";
    private static final String KEY_FAVORITES = "favorites";

    public static void saveStationInfo(Context context,
                                       String stationName,
                                       String lineNumber,
                                       String stationCode) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("station_nm", normalize(stationName));   // ← 저장은 역 없이
            obj.put("line_num", lineNumber);
            obj.put("station_cd", stationCode);

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_INFO, obj.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 역 제거 + 좌우 공백 제거
    private static String normalize(String name) {
        if (name == null) return "";
        name = name.trim();

        if (name.endsWith("역"))
            name = name.substring(0, name.length() - 1);

        return name;
    }

    // 출력할 때 역 붙임
    public static String getStation(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");

            if (jsonString.isEmpty()) return "서울역";

            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("station_nm") + "역";   // ← 출력 전 붙이기

        } catch (Exception e) {
            e.printStackTrace();
            return "서울역";
        }
    }

    public static String getStationCode(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");
            if (jsonString.isEmpty()) return "";

            return new JSONObject(jsonString).getString("station_cd");

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLine(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String jsonString = prefs.getString(KEY_INFO, "");
            if (jsonString.isEmpty()) return "1호선";

            JSONObject obj = new JSONObject(jsonString);
            String raw = obj.getString("line_num");

            raw = raw.replace("호선", "");
            int n = Integer.parseInt(raw);

            return n + "호선";

        } catch (Exception e) {
            e.printStackTrace();
            return "1호선";
        }
    }

    public static List<String> getFavorites(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(KEY_FAVORITES, "[]");

        List<String> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getString(i));
            }
        } catch (Exception ignored) {}

        return list;
    }

    public static void saveFavorites(Context context, List<String> list) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        JSONArray arr = new JSONArray();
        for (String s : list) arr.put(s);

        edit.putString(KEY_FAVORITES, arr.toString());
        edit.apply();
    }

    public static void addFavorite(Context context, String stationName, String lineNumber, String stationCode) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        // 현재 저장된 즐겨찾기 리스트 불러오기
        String json = pref.getString(KEY_FAVORITES, "[]");
        List<String> list = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getString(i));
            }
        } catch (Exception ignored) {}

        String raw = lineNumber + "|" + stationName + "|" + stationCode;

        // 중복 저장 방지
        if (!list.contains(raw)) {
            list.add(raw);
        }

        JSONArray arr = new JSONArray();
        for (String s : list) arr.put(s);

        edit.putString(KEY_FAVORITES, arr.toString());
        edit.apply();
    }

    public static void removeFavorite(Context context, String rawItem) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();

        String json = pref.getString(KEY_FAVORITES, "[]");
        List<String> list = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                String item = arr.getString(i);
                if (!item.equals(rawItem)) {   // 클릭한 raw data와 동일할 때만 제거
                    list.add(item);
                }
            }
        } catch (Exception ignored) {}

        JSONArray newArr = new JSONArray();
        for (String s : list) newArr.put(s);

        edit.putString(KEY_FAVORITES, newArr.toString());
        edit.apply();
    }

}
