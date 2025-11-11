package smu.ai.transittime;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private Button btnTimetable, btnSchedule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        spinnerLine = view.findViewById(R.id.spinnerLine);
        editStation = view.findViewById(R.id.editStation);
        btnTimetable = view.findViewById(R.id.btnTimetable);
        btnSchedule = view.findViewById(R.id.btnSchedule);

        // ✅ 노선 목록
        List<String> subwayLines = new ArrayList<>();
        subwayLines.add("호선을 선택하세요");
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

        // ✅ Adapter (placeholder + 색상)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subwayLines
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (position == 0) {
                    textView.setTextColor(0xFF9E9E9E); // placeholder 색상
                } else {
                    textView.setTextColor(0xFF64B5F6); // 선택 항목
                }
                textView.setTextSize(16);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextSize(16);
                if (position == 0) {
                    textView.setTextColor(0xFF9E9E9E);
                } else {
                    textView.setTextColor(0xFF000000);
                }
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLine.setAdapter(adapter);
        spinnerLine.setSelection(0);

        // ✅ 버튼 클릭 시 화면 전환
        btnTimetable.setOnClickListener(v -> {
            if (!validateInput()) return;
            switchToFragment(new TimetableFragment());
        });

        btnSchedule.setOnClickListener(v -> {
            if (!validateInput()) return;
            switchToFragment(new ScheduleFragment());
        });

        return view;
    }

    private boolean validateInput() {
        int pos = spinnerLine.getSelectedItemPosition();
        String stationName = editStation.getText().toString();

        if (pos == 0) {
            Toast.makeText(requireContext(), "호선을 선택해주세요!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (stationName.isEmpty()) {
            Toast.makeText(requireContext(), "역 이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void switchToFragment(Fragment fragment) {
        String selectedLine = spinnerLine.getSelectedItem().toString();
        String stationName = editStation.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("line", selectedLine);
        bundle.putString("station", stationName);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}