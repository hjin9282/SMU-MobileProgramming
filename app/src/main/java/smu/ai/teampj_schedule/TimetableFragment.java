package smu.ai.teampj_schedule;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ai.teampj_schedule.api.RetrofitClient;
import smu.ai.teampj_schedule.api.SubwayApiService;
import smu.ai.teampj_schedule.model.TimeItem;
import smu.ai.teampj_schedule.model.TimeTableResponse;
import smu.ai.teampj_schedule.model.TimeTableRow;

public class TimetableFragment extends Fragment {

    private TextView tabUp, tabDown, txtStation;
    private RecyclerView recyclerView;
    private TimeTableAdapter adapter;

    private String stationLine;
    private String stationCode;
    private String stationName;
    private String week = "1";   // 1: 평일 | 2: 토요앨 | 3: 일요일, 공휴일
    private String API_KEY = BuildConfig.TIMETABLE_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        // UI 연결
        txtStation = view.findViewById(R.id.txtStation);
        tabUp = view.findViewById(R.id.tabUp);
        tabDown = view.findViewById(R.id.tabDown);
        recyclerView = view.findViewById(R.id.recyclerTimeTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TimeTableAdapter();
        recyclerView.setAdapter(adapter);

        // 선택한 역 정보 가져오기
        stationLine = PreferenceManager.getLine(getContext());
        stationName = PreferenceManager.getStation(getContext());
        stationCode = PreferenceManager.getStationCode(getContext());

        txtStation.setText(stationLine + " "  + stationName);

        // 기본 선택 = 상행
        selectTab(true);
        loadTimeTable("1");  // 상행

        tabUp.setOnClickListener(v -> {
            selectTab(true);
            loadTimeTable("1");
        });

        tabDown.setOnClickListener(v -> {
            selectTab(false);
            loadTimeTable("2");
        });

        return view;
    }

    private void selectTab(boolean up) {
        if (up) {
            tabUp.setSelected(true);
            tabDown.setSelected(false);

            tabUp.setTypeface(null, Typeface.BOLD);
            tabDown.setTypeface(null, Typeface.NORMAL);

        } else {
            tabUp.setSelected(false);
            tabDown.setSelected(true);

            tabDown.setTypeface(null, Typeface.BOLD);
            tabUp.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void loadTimeTable(String inout) {

        SubwayApiService api = RetrofitClient.getClient().create(SubwayApiService.class);

        api.getTimeTable(
                API_KEY,
                1,
                300,
                stationCode,
                week,
                inout
        ).enqueue(new Callback<TimeTableResponse>() {

            @Override
            public void onResponse(Call<TimeTableResponse> call, Response<TimeTableResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                List<TimeTableRow> rows =
                        response.body().SearchSTNTimeTableByIDService.row;

                List<TimeItem> items = new ArrayList<>();

                for (TimeTableRow r : rows) {
                    String t = r.LEFTTIME.substring(0, 5);
                    String dest = r.SUBWAYENAME;
                    boolean express = "D".equals(r.EXPRESS_YN);

                    items.add(new TimeItem(t, dest, express));
                }

                // 시간순 정렬
                Collections.sort(items, (a, b) -> a.time.compareTo(b.time));

                // 시간별 그룹핑
                List<Object> finalList = groupByHour(items);

                requireActivity().runOnUiThread(() -> {
                    adapter.setItems(finalList);
                });
            }

            @Override
            public void onFailure(Call<TimeTableResponse> call, Throwable t) {}
        });

    }

    private List<Object> groupByHour(List<TimeItem> list) {
        List<Object> result = new ArrayList<>();
        String lastHour = "";

        for (TimeItem item : list) {
            String hour = item.time.substring(0, 2);

            if (!hour.equals(lastHour)) {
                result.add(hour);
                lastHour = hour;
            }

            result.add(item);
        }

        return result;
    }
}
