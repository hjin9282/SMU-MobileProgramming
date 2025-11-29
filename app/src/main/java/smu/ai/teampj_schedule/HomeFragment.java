package smu.ai.teampj_schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HomeFragment extends Fragment {

    private Spinner spinnerLine;
    private TextView tvSelectedLine;
    private EditText editTextStation;
    private Button btnSave;
    private ImageView dropDownIcon;
    private LinearLayout lineBox;

    private boolean userSelected = false;


    private String loadJSONFromAsset() {
        try {
            InputStream is = requireContext().getAssets().open("stations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private JSONObject findStationInfo(String line, String station) {
        try {
            String jsonStr = loadJSONFromAsset();
            JSONObject root = new JSONObject(jsonStr);
            JSONArray data = root.getJSONArray("DATA");

            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);

                String fileLine = obj.getString("line_num");
                String fileStation = obj.getString("station_nm");

                if (fileLine.equals(line) && fileStation.equals(station)) {
                    return obj;  // 매칭된 역 정보 반환
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 못 찾으면 null
    }

    private String convertLine(String line) {
        // "2호선" → 2 추출
        String num = line.replace("호선", "");

        // "02호선" 형태로 맞추기
        if (num.length() == 1) {
            num = "0" + num;
        }

        return num + "호선";
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        spinnerLine = view.findViewById(R.id.spinnerLine);
        tvSelectedLine = view.findViewById(R.id.tvSelectedLine);
        dropDownIcon = view.findViewById(R.id.dropDownIcon);
        editTextStation = view.findViewById(R.id.editTextStation);
        btnSave = view.findViewById(R.id.btnSave);
        lineBox = view.findViewById(R.id.lineBox);

        setupSpinner();
        setupEvents();
    }

    @Override
    public void onResume() {
        super.onResume();

        tvSelectedLine.setText("호선을 선택하세요");
        editTextStation.setText("");
        spinnerLine.setSelection(0);
        userSelected = false;
    }

    private void setupSpinner() {
        String[] lines = {
                "호선을 선택하세요",   // 0번 인덱스 → 안내문구 (비활성화)
                "1호선", "2호선", "3호선", "4호선",
                "5호선", "6호선", "7호선", "8호선", "9호선"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                lines
        ) {
            @Override
            public boolean isEnabled(int position) {
                // 0번 인덱스 선택 불가 처리
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                // 안내문구는 회색 처리
                TextView tv = (TextView) view;

                view.setBackgroundColor(getResources().getColor(R.color.black, null));    // 검정 배경

                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.gray, null));       // 회색
                } else {
                    tv.setTextColor(getResources().getColor(R.color.skyBlue, null));    // 하늘색
                }

                return view;
            }
        };

        spinnerLine.setAdapter(adapter);
    }

    private void setupEvents() {

        View.OnClickListener openDropdown = v -> {
            userSelected = true;
            spinnerLine.performClick();
        };

        dropDownIcon.setOnClickListener(openDropdown);
        tvSelectedLine.setOnClickListener(openDropdown);
        lineBox.setOnClickListener(openDropdown);

        spinnerLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 안내문구(0번) 선택 시 무시
                if (position == 0) {
                    tvSelectedLine.setText("호선을 선택하세요");
                    return;
                }

                // 실제 선택일 때만 반영
                String selected = parent.getItemAtPosition(position).toString();
                tvSelectedLine.setText(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(v -> saveValues());
    }

    private void saveValues() {

        String line = convertLine(tvSelectedLine.getText().toString());
        String stationRaw = editTextStation.getText().toString().trim();
        String station = PreferenceManager.normalizeStationName(stationRaw);

        if (line.equals("호선을 선택하세요")) {
            Toast.makeText(requireContext(), "호선을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (station.isEmpty()) {
            Toast.makeText(requireContext(), "역 이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // JSON에서 역 정보 찾기
        JSONObject stationInfo = findStationInfo(line, station);

        if (stationInfo == null) {
            Toast.makeText(requireContext(), "해당 역을 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // JSON 문자열로 저장
        SharedPreferences prefs = requireContext().getSharedPreferences("subgo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("station_info", stationInfo.toString());
        editor.apply();

        Toast.makeText(requireContext(), "역 정보 저장 완료!", Toast.LENGTH_SHORT).show();
    }
}
