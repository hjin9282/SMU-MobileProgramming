package smu.ai.teampj_schedule.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StationUtils {

    // 역 정보를 담을 내부 클래스
    public static class StationNode {
        String name;
        String code; // fr_code (순서 정렬용)

        public StationNode(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

    // JSON 파일에서 해당 호선의 역 리스트를 가져와서 'fr_code' 순으로 정렬
    public static List<StationNode> getStationList(Context context, String lineName) {
        List<StationNode> stationList = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("stations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA"); // "DATA" 배열 가져오기

            // 호선 이름 포맷 맞추기 (예: "1호선" -> "01호선")
            String formattedLine = formatLineName(lineName);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String jsonLine = obj.getString("line_num");

                // 해당 호선만 담기
                if (jsonLine.equals(formattedLine)) {
                    String name = obj.getString("station_nm");
                    String code = obj.getString("fr_code");
                    stationList.add(new StationNode(name, code));
                }
            }

            // 외부코드(fr_code) 기준으로 정렬해야 실제 역 순서
            Collections.sort(stationList, new Comparator<StationNode>() {
                @Override
                public int compare(StationNode o1, StationNode o2) {
                    return o1.code.compareTo(o2.code);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationList;
    }

    // "1호선" -> "01호선" 처럼 JSON 파일 형식에 맞춰주는 함수
    private static String formatLineName(String line) {
        if (line.matches("\\d호선")) { // 숫자가 1개인 경우 (1호선~9호선)
            return "0" + line;
        }
        return line;
    }

    // 현재 역을 기준으로 "이전 3개 역 + 현재 역" 이름을 리스트로 반환
    public static List<String> getPrevStations(Context context, String line, String currentStation, boolean isUpLine) {
        List<StationNode> allStations = getStationList(context, line);
        List<String> result = new ArrayList<>();

        // 현재 역 찾기
        int currentIndex = -1;
        for (int i = 0; i < allStations.size(); i++) {
            // "서울역" vs "서울" 같은 이름 불일치 해결을 위해 포함 여부나 "역" 제거 후 비교 추천
            String nodeName = allStations.get(i).name;
            if (nodeName.equals(currentStation) || (nodeName + "역").equals(currentStation) || nodeName.equals(currentStation.replace("역", ""))) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) return result; // 못 찾음

        // 인덱스로 이전 역 3개 찾기
        // 상행(0) vs 하행(1)
        // fr_code 정렬 기준: 100(소요산) ... -> ... 133(서울역) ... -> ... 161(인천)
        // 하행(1, 인천방향): 인덱스가 커지는 방향. (내 이전 역은 index - 1, -2, -3)
        // 상행(0, 소요산방향): 인덱스가 작아지는 방향. (내 이전 역은 index + 1, +2, +3 ... 거꾸로 옴)

        if (!isUpLine) { // 하행 (1) - 숫자가 커지는 방향으로 가는 중 -> 이전 역은 작은 숫자들
            addStationSafe(result, allStations, currentIndex - 3);
            addStationSafe(result, allStations, currentIndex - 2);
            addStationSafe(result, allStations, currentIndex - 1);
        } else { // 상행 (0) - 숫자가 작아지는 방향으로 가는 중 -> 이전 역은 큰 숫자들
            addStationSafe(result, allStations, currentIndex + 3);
            addStationSafe(result, allStations, currentIndex + 2);
            addStationSafe(result, allStations, currentIndex + 1);
        }

        // 마지막은 현재 역
        result.add(currentStation);

        return result;
    }

    // 리스트 범위 안벗어나게
    private static void addStationSafe(List<String> result, List<StationNode> list, int index) {
        if (index >= 0 && index < list.size()) {
            result.add(list.get(index).name);
        } else {
            // 순환선(2호선)인 경우 여기서 index % size
            // 일단은 빈 문자열 처리
            result.add("");
        }
    }
}