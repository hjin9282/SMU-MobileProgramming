package smu.ai.teampj_schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ai.teampj_schedule.api.RetrofitClient;
import smu.ai.teampj_schedule.api.SubwayApiService;
import smu.ai.teampj_schedule.model.RealtimeArrival;
import smu.ai.teampj_schedule.model.RealtimeResponse;

public class ScheduleFragment extends Fragment {

    private TextView tvTitle, tvUpTime, tvUpLocation, tvDownTime, tvDownLocation;
    // API 키 (실시간 도착 API와 동일한 키 사용)
    private static final String API_KEY = "7062466f5564613233336866795056";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // 뷰 연결
        tvTitle = view.findViewById(R.id.tvTitle);
        tvUpTime = view.findViewById(R.id.tvUpTime);
        tvUpLocation = view.findViewById(R.id.tvUpLocation);
        tvDownTime = view.findViewById(R.id.tvDownTime);
        tvDownLocation = view.findViewById(R.id.tvDownLocation);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedule();
    }

    private void loadSchedule() {
        String station = PreferenceManager.getStation(getContext());
        String line = PreferenceManager.getLine(getContext());

        tvTitle.setText(line + " " + station);

        // URL을 직접 만들기
        // 역 이름(station)은 URL 인코딩이 필요할 수 있지만, Retrofit이 웬만하면 처리해줌.
        // 혹시 안 되면 station 부분을 URLEncoder.encode(station, "UTF-8") 해야 함.
        String fullUrl = "http://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimeStationArrival/0/10/" + station;

        SubwayApiService api = RetrofitClient.getClient().create(SubwayApiService.class);

        // 수정된 메서드 호출 (전체 URL을 넘겨줌)
        api.getRealtimeArrivals(fullUrl).enqueue(new Callback<RealtimeResponse>() {
            @Override
            public void onResponse(Call<RealtimeResponse> call, Response<RealtimeResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().realtimeArrivalList != null) {
                    updateUI(response.body().realtimeArrivalList);
                } else {
                    tvUpLocation.setText("도착 정보가 없습니다.");
                    tvDownLocation.setText("도착 정보가 없습니다.");
                }
            }

            @Override
            public void onFailure(Call<RealtimeResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("API", "실시간 정보 실패: " + t.getMessage());
                tvUpLocation.setText("네트워크 오류");
            }
        });
    }

    private void updateUI(List<RealtimeArrival> arrivals) {
        // 변수 초기화
        String upTime = "정보 없음";
        String upLoc = "";
        String downTime = "정보 없음";
        String downLoc = "";

        for (RealtimeArrival arrival : arrivals) {
            // updnLine: "0"=상행/내선, "1"=하행/외선
            if (arrival.updnLine.equals("0")) {
                // 가장 빨리 오는 열차 하나만 표시 (정보가 없을 때만 갱신)
                if (upTime.equals("정보 없음")) {
                    upTime = arrival.arvlMsg2; // 예: "3분 후" or "전역 도착"
                    upLoc = arrival.trainLineNm + " (" + arrival.currentLocation + ")";
                }
            } else {
                if (downTime.equals("정보 없음")) {
                    downTime = arrival.arvlMsg2;
                    downLoc = arrival.trainLineNm + " (" + arrival.currentLocation + ")";
                }
            }
        }

        tvUpTime.setText(upTime);
        tvUpLocation.setText(upLoc);
        tvDownTime.setText(downTime);
        tvDownLocation.setText(downLoc);
    }
}