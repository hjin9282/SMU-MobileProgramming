package smu.ai.teampj_schedule;

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

import smu.ai.teampj_schedule.api.RetrofitClient;
import smu.ai.teampj_schedule.api.SubwayApiService;
import smu.ai.teampj_schedule.model.StationResponse;
import smu.ai.teampj_schedule.model.StationRow;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private Spinner spinnerLine;
    private EditText editStation;
    private Button btnSearch;

    // 호선별 역 목록을 저장 (키: "1호선", 값: ["서울역", "시청"...])
    private Map<String, List<String>> lineStationMap = new HashMap<>();

    // API 키는 key만 사용 (URL 형태 X)
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

    /*
        1) API 호출 -> 역 데이터 가져오기 (1-9호선 필터링 및 데이터 없을 시 더미 로드)
    */
    private void loadStationData() {
        SubwayApiService api = RetrofitClient.getClient().create(SubwayApiService.class);

        api.getStations(API_KEY).enqueue(new Callback<StationResponse>() {
            @Override
            public void onResponse(Call<StationResponse> call, Response<StationResponse> response) {

                // 1. 통신 실패 또는 기본 응답 바디가 없을 경우
                if (!response.isSuccessful() || response.body() == null || response.body().searchInfo == null) {
                    Log.e("API", "데이터 없음, 가짜 데이터 사용합니다.");
                    loadDummyData();
                    setupSpinner();
                    return;
                }

                List<StationRow> rows = response.body().searchInfo.rows;
                lineStationMap.clear();

                // 2. 데이터가 있다면 필터링 진행
                if (rows != null) {
                    for (StationRow row : rows) {
                        String lineNumber = row.lineNumber;
                        String line;

                        // '1'~'9' 또는 '01'~'09' 패턴 허용
                        if (lineNumber != null && lineNumber.matches("^(0?[1-9])$")) {
                            line = lineNumber.replaceAll("^0", "") + "호선";
                        } else {
                            continue;
                        }

                        String station = row.stationName;

                        if (!lineStationMap.containsKey(line)) {
                            lineStationMap.put(line, new ArrayList<>());
                        }
                        lineStationMap.get(line).add(station);
                    }
                }

                // 3. API 통신은 성공했으나, 필터링 결과 데이터가 하나도 없는 경우 처리
                if (lineStationMap.isEmpty()) {
                    Log.d("API", "필터링 후 데이터 없음, 가짜 데이터 로드");
                    loadDummyData();
                }

                setupSpinner();
            }

            @Override
            public void onFailure(Call<StationResponse> call, Throwable t) {
                Log.e("API", "통신 실패: " + t.getMessage());
                loadDummyData();
                setupSpinner();
            }
        });
    }

    /*
        2) Spinner 설정 (숫자 순 정렬)
    */
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
                R.layout.spinner_item_selected,
                lines
        );

        // 드롭다운 목록
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

        spinnerLine.setAdapter(adapter);
    }

    /*
        3) 저장하기 버튼 (역 이름 검증 로직 추가)
    */
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

            // API에서 가져온 역 목록에 사용자가 입력한 역이 있는지 검증합니다.
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


    // API가 안 될 때 사용할 비상용 가짜 데이터
    private void loadDummyData() {
        lineStationMap.clear();

        // 1호선 데이터 강제 주입
        ArrayList<String> line1 = new ArrayList<>();
        line1.add("서울역");
        line1.add("시청역");
        line1.add("종각역");
        line1.add("청량리역");
        lineStationMap.put("1호선", line1);

        // 4호선 데이터 강제 주입
        ArrayList<String> line4 = new ArrayList<>();
        line4.add("숙대입구역");
        line4.add("서울역역");
        line4.add("삼각지역");
        line4.add("명동역");
        lineStationMap.put("4호선", line4);
    }
}

