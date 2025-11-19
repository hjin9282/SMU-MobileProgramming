package smu.ai.transittime;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import smu.ai.transittime.api.RetrofitClient;
import smu.ai.transittime.api.SubwayApiService;
import smu.ai.transittime.model.StationResponse;
import smu.ai.transittime.model.StationRow;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private Spinner spinnerLine;
    private EditText editStation;
    private Button btnSearch;

    // "1호선": ["서울역", "시청", ...]
    private Map<String, List<String>> lineStationMap = new HashMap<>();

    // ✔ API 키는 "키만" 사용 — 절대 URL 형태 넣으면 안됨
    private static final String API_KEY = "7062466f5564613233336866795056";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerLine = view.findViewById(R.id.spinnerLine);
        editStation = view.findViewById(R.id.editStation);
        btnSearch = view.findViewById(R.id.btnSearch);

        // API 데이터 로드
        loadStationData();

        // 버튼
        setupButton();

        return view;
    }


    // ---------------------------------------------------
    // 🔵 1) API 호출 → 역 데이터 가져오기 (수정: 1-9호선 필터링)
    // ---------------------------------------------------
    private void loadStationData() {

        SubwayApiService api = RetrofitClient.getClient().create(SubwayApiService.class);

        api.getStations(API_KEY).enqueue(new Callback<StationResponse>() {
            @Override
            public void onResponse(Call<StationResponse> call, Response<StationResponse> response) {

                if (!response.isSuccessful() || response.body() == null || response.body().searchInfo == null) {
                    Log.e("API", "응답 실패 / null");
                    return;
                }

                List<StationRow> rows = response.body().searchInfo.rows;

                lineStationMap.clear();

                for (StationRow row : rows) {

                    String lineNumber = row.lineNumber;
                    String line;

                    // 🚨 최종 수정: '1'~'9' 또는 '01'~'09' 패턴을 모두 허용
                    if (lineNumber != null && lineNumber.matches("^(0?[1-9])$")) {
                        // 노선 번호에서 앞의 0을 제거하고 '호선'을 붙입니다. (예: "01" -> "1호선")
                        line = lineNumber.replaceAll("^0", "") + "호선";
                    } else {
                        // 1-9호선 외의 노선은 무시
                        continue;
                    }

                    String station = row.stationName;

                    // 📌 디버깅용: 필터링된 데이터가 무엇인지 확인
                    // Log.d("SubwayData", "라인: " + line + ", 역: " + station);

                    if (!lineStationMap.containsKey(line)) {
                        lineStationMap.put(line, new ArrayList<>());
                    }

                    lineStationMap.get(line).add(station);
                }

                setupSpinner();
            }

            @Override
            public void onFailure(Call<StationResponse> call, Throwable t) {
                Log.e("API", "통신 실패: " + t.getMessage());
                // 통신 실패 시에도 Spinner를 초기화하여 사용자에게 시각적 피드백 제공
                setupSpinner();
            }
        });
    }



    // ---------------------------------------------------
    // 🔵 2) Spinner 설정 (수정: 숫자 순 정렬)
    // ---------------------------------------------------
    private void setupSpinner() {
        List<String> lines = new ArrayList<>(lineStationMap.keySet());

        // "1호선", "2호선"과 같이 숫자 순으로 정렬
        Collections.sort(lines, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    // "1호선"에서 "1"을 추출하여 숫자로 비교합니다.
                    int num1 = Integer.parseInt(s1.replaceAll("[^0-9]", ""));
                    int num2 = Integer.parseInt(s2.replaceAll("[^0-9]", ""));
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    return s1.compareTo(s2);
                }
            }
        });

        lines.add(0, "호선을 선택하세요");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                lines
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLine.setAdapter(adapter);
    }


    // ---------------------------------------------------
    // 🔵 3) 저장하기 버튼 (수정: 역 이름 검증 로직 추가)
    // ---------------------------------------------------
    private void setupButton() {

        btnSearch.setOnClickListener(v -> {

            if (spinnerLine.getSelectedItem() == null) {
                Toast.makeText(getContext(), "호선을 불러오는 중입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            String line = spinnerLine.getSelectedItem().toString();
            // 입력된 역 이름의 앞뒤 공백 제거
            String station = editStation.getText().toString().trim();

            if (line.equals("호선을 선택하세요")) {
                Toast.makeText(getContext(), "호선을 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (station.isEmpty()) {
                Toast.makeText(getContext(), "역 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ API에서 가져온 역 목록에 사용자가 입력한 역이 있는지 검증합니다.
            List<String> stationsInLine = lineStationMap.get(line);

            if (stationsInLine == null || !stationsInLine.contains(station)) {
                // 입력된 역 이름이 해당 노선에 존재하지 않는 경우
                String errorMsg = String.format("입력하신 '%s'역은 '%s'에 존재하지 않거나 역 이름이 정확하지 않습니다.", station, line);
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences prefs =
                    requireActivity().getSharedPreferences("subway", MODE_PRIVATE);

            prefs.edit()
                    .putString("line", line)
                    .putString("station", station)
                    .apply();

            Toast.makeText(getContext(), "저장되었습니다!", Toast.LENGTH_SHORT).show();
        });
    }
}