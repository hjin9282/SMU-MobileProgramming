package smu.ai.transittime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Spinner spinnerLine;
    private EditText editStation;
    private Button btnSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerLine = view.findViewById(R.id.spinnerLine);
        editStation = view.findViewById(R.id.editStation);
        btnSearch = view.findViewById(R.id.btnSearch);

        // ✅ 노선 목록 (안내 문구 포함)
        List<String> subwayLines = new ArrayList<>();
        subwayLines.add("호선을 선택해주세요"); // 안내문
        subwayLines.add("1호선");
        subwayLines.add("2호선");
        subwayLines.add("3호선");
        subwayLines.add("4호선");
        subwayLines.add("5호선");
        subwayLines.add("6호선");
        subwayLines.add("7호선");
        subwayLines.add("8호선");
        subwayLines.add("9호선");
        subwayLines.add("수인분당선");
        subwayLines.add("신분당선");
        subwayLines.add("경의중앙선");
        subwayLines.add("공항철도");
        subwayLines.add("경춘선");
        subwayLines.add("의정부경전철");
        subwayLines.add("에버라인");
        subwayLines.add("신림선");
        subwayLines.add("우이신설선");

        // ✅ ArrayAdapter 설정 (커스텀 스타일)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subwayLines
        ) {
            @Override
            public boolean isEnabled(int position) {
                // 첫 번째 항목(안내문)은 비활성화
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    // 안내문: 회색 표시
                    view.setTextColor(0xFF9E9E9E);
                } else {
                    // 실제 항목: 검정색 표시
                    view.setTextColor(0xFF000000);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLine.setAdapter(adapter);

        // ✅ 기본 선택은 안내문
        spinnerLine.setSelection(0);

        // ✅ 버튼 클릭 시 동작
        btnSearch.setOnClickListener(v -> {
            int pos = spinnerLine.getSelectedItemPosition();
            String stationName = editStation.getText().toString();

            if (pos == 0) {
                Toast.makeText(requireContext(), "호선을 선택해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stationName.isEmpty()) {
                Toast.makeText(requireContext(), "역 이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedLine = spinnerLine.getSelectedItem().toString();

            Bundle bundle = new Bundle();
            bundle.putString("line", selectedLine);
            bundle.putString("station", stationName);

            TimetableFragment timetableFragment = new TimetableFragment();
            timetableFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, timetableFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
