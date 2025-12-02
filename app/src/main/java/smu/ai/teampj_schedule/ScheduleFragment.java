package smu.ai.teampj_schedule;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smu.ai.teampj_schedule.api.RetrofitClient;
import smu.ai.teampj_schedule.api.SubwayApiService;
import smu.ai.teampj_schedule.model.RealtimeArrival;
import smu.ai.teampj_schedule.model.RealtimeResponse;
import smu.ai.teampj_schedule.model.StationUtils;

public class ScheduleFragment extends Fragment {

    private TextView tvTitle, tvUpTime, tvUpLocation, tvDownTime, tvDownLocation;
    private String API_KEY = BuildConfig.SCHEDULE_API_KEY;
    private FrameLayout flUpTrainContainer, flDownTrainContainer;
    private View viewUpTrack, viewDownTrack;

    private TextView tvUpSt3, tvUpSt2, tvUpSt1, tvUpSt0;
    private TextView tvDownSt3, tvDownSt2, tvDownSt1, tvDownSt0;
    private Handler handler = new Handler(Looper.getMainLooper());

    // 전역 변수로 현재 열차 리스트를 저장해둬야 1초마다 꺼내 쓸 수 있음
    private List<RealtimeArrival> currentUpTrains = new ArrayList<>();
    private List<RealtimeArrival> currentDownTrains = new ArrayList<>();


    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            // API 호출 (데이터 갱신)
            loadSchedule();

            // 다음 실행은 10초 뒤 (API 부하 방지)
            handler.postDelayed(this, 10000);
        }
    };

    // 1초마다 위치만 부드럽게 옮겨주는 러너블
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            // 저장해둔 리스트의 시간을 1초씩 줄임 (수동 카운트다운)
            tickDownTime(currentUpTrains);
            tickDownTime(currentDownTrains);

            // 화면 다시 그리기
            renderTrains(flUpTrainContainer, currentUpTrains);
            renderTrains(flDownTrainContainer, currentDownTrains);

            // 1초 뒤에 또 실행 (무한 반복)
            handler.postDelayed(this, 1000);
        }
    };

    // 시간 깎는 헬퍼 함수
    private void tickDownTime(List<RealtimeArrival> trains) {
        if (trains == null) return;
        for (RealtimeArrival train : trains) {
            try {
                int currentSec = Integer.parseInt(train.barvlDt);
                // 1초 줄임
                train.barvlDt = String.valueOf(currentSec - 1);
            } catch (Exception e) { /* 무시 */ }
        }
    }


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

        viewUpTrack = view.findViewById(R.id.viewUpTrack);
        viewDownTrack = view.findViewById(R.id.viewDownTrack);

        tvUpSt3 = view.findViewById(R.id.tvUpSt3);
        tvUpSt2 = view.findViewById(R.id.tvUpSt2);
        tvUpSt1 = view.findViewById(R.id.tvUpSt1);
        tvUpSt0 = view.findViewById(R.id.tvUpSt0);
        tvDownSt3 = view.findViewById(R.id.tvDownSt3);
        tvDownSt2 = view.findViewById(R.id.tvDownSt2);
        tvDownSt1 = view.findViewById(R.id.tvDownSt1);
        tvDownSt0 = view.findViewById(R.id.tvDownSt0);

        flUpTrainContainer = view.findViewById(R.id.flUpTrainContainer);
        flDownTrainContainer = view.findViewById(R.id.flDownTrainContainer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedule();
        handler.post(refreshRunnable); // API 루프 시작
        handler.post(animationRunnable); // 애니메이션 루프 시작
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
        handler.removeCallbacks(animationRunnable); // 화면 꺼지면 애니메이션도 중단
    }

    private void loadSchedule() {
        // 호선, 역 정보
        String line = PreferenceManager.getLine(getContext());
        String station = PreferenceManager.getStation(getContext());

        tvTitle.setText(line + " " + station);

        // 정적 역 이름 세팅
        setStationNames(line, station);

        // "역" 글자가 끝에 있으면 떼기 (예: 숙대입구역 -> 숙대입구)
        if (station.endsWith("역")) {
            station = station.substring(0, station.length() - 1);
        }
        // 예외 처리: "서울"이 되어버리면 안 되니까 "서울역"은 다시 붙여줘야
        if (station.equals("서울")) station = "서울역";

        try {
            // 역 이름 한글 깨짐 방지 (인코딩)
            String encodedStation = URLEncoder.encode(station, "UTF-8");

            // 전체 주소 직접 만들기 (http://swopenapi... 로 시작하는 주소)
            // TimeTable 주소(openapi)와 겹침 방지
            String fullUrl = "http://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimeStationArrival/0/10/" + encodedStation;
            SubwayApiService api = RetrofitClient.getClient().create(SubwayApiService.class);

            // 만든 전체 주소를 넣어서 호출
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 역 이름 세팅 함수
    private void setStationNames(String line, String currentStation) {
        // 역 이름 통일 ("역" 떼기/붙이기 로직이 JSON이랑 맞아야 함)

        // 1. 상행선 (UpLine = true) 역 이름 가져오기
        List<String> upNames = StationUtils.getPrevStations(getContext(), line, currentStation, true);
        if (upNames.size() == 4) {
            tvUpSt3.setText(upNames.get(0));
            tvUpSt2.setText(upNames.get(1));
            tvUpSt1.setText(upNames.get(2));
            tvUpSt0.setText(upNames.get(3));
        }

        // 2. 하행선 (UpLine = false) 역 이름 가져오기
        List<String> downNames = StationUtils.getPrevStations(getContext(), line, currentStation, false);
        if (downNames.size() == 4) {
            tvDownSt3.setText(downNames.get(0));
            tvDownSt2.setText(downNames.get(1));
            tvDownSt1.setText(downNames.get(2));
            tvDownSt0.setText(downNames.get(3));
        }
    }

    // 초(String)를 받아서 "X분 Y초 후"로 바꿔주는 함수
    private String formatSeconds(String secondsStr) {
        try {
            int totalSeconds = Integer.parseInt(secondsStr);

            // 0초 이하면 "도착" 이라고 알려줘야 함
            if (totalSeconds <= 0) return "도착 / 진입";

            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            if (minutes > 0) {
                return minutes + "분 " + seconds + "초 후 도착";
            } else {
                return seconds + "초 후 도착";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private void updateUI(List<RealtimeArrival> arrivals) {
        // 리스트 초기화
        currentUpTrains.clear();
        currentDownTrains.clear();

        String upTime = "정보 없음";
        String downTime = "정보 없음";

        for (RealtimeArrival arrival : arrivals) {
            int seconds = 0;
            try {
                seconds = Integer.parseInt(arrival.barvlDt);
            } catch (Exception e) {
            }

            // 이미 도착해서 3초 지난 건 삭제
            if (seconds < -3) continue; 

            if (arrival.updnLine.equals("0") || arrival.updnLine.equals("상행") || arrival.updnLine.equals("내선")) {
                currentUpTrains.add(arrival);
                if (upTime.equals("정보 없음") || Integer.parseInt(arrival.barvlDt) < 600) {
                    String formatted = formatSeconds(arrival.barvlDt);
                    if (!formatted.isEmpty()) upTime = formatted;
                }
            } else { // 하행
                currentDownTrains.add(arrival);
                if (downTime.equals("정보 없음") || Integer.parseInt(arrival.barvlDt) < 600) {
                    String formatted = formatSeconds(arrival.barvlDt);
                    if (!formatted.isEmpty()) downTime = formatted;
                }
            }
        }

        tvUpTime.setText(upTime);
        tvDownTime.setText(downTime);

        // 바로 그리기
        renderTrains(flUpTrainContainer, currentUpTrains);
        renderTrains(flDownTrainContainer, currentDownTrains);
    }

    private void renderTrains(FrameLayout container, List<RealtimeArrival> trains) {
        if (container == null) return;
        container.removeAllViews(); // 기존 기차 지우기

        int trackWidth = container.getWidth();
        if (trackWidth == 0) return;

        // 전체 구간 시간 (10분 = 600초)
        final float MAX_SECONDS = 600f;

        for (RealtimeArrival train : trains) {
            // 1. 시간 파싱 (안전하게)
            int seconds = 0;
            try {
                seconds = Integer.parseInt(train.barvlDt);
            } catch (NumberFormatException e) {
                continue;
            }

            // 2. 기본 위치 계산 (시간 기반)
            // 시간이 많을수록(멀수록) 0(왼쪽), 적을수록(가까울수록) 1(오른쪽)
            float progress;
            if (seconds > MAX_SECONDS) {
                progress = 0.0f; // 10분 이상 남았으면 맨 왼쪽 대기
            } else {
                progress = 1.0f - ((float) seconds / MAX_SECONDS);
            }

            // 3. 상태별 위치 보정 (Snap)
            // "1"(도착) 상태일 때만 역 위치에 자석처럼 붙임.
            // "0"(진입) 상태는 그냥 시간대로 움직이게 둬야 부드럽게 들어감!
            if (train.arvlCd.equals("1")) {
                // 현재 진행률에 맞춰서 가장 가까운 역(점)으로 고정
                if (progress > 0.85f) progress = 1.0f;        // 현재역 도착
                else if (progress > 0.55f) progress = 0.666f; // 전역 도착
                else if (progress > 0.25f) progress = 0.333f; // 2정거장 전 도착
                else progress = 0.0f;                         // 3정거장 전
            }

            // 시간이 0초인데 아직 도착 코드("1")가 안 떴다면 (이동 중)
            // 도착한 것처럼 100%에 두지 말고, 살짝 전(99%)에 둬서 진입 느낌 내기
            if (seconds <= 0 && !train.arvlCd.equals("1")) {
                if (progress >= 1.0f) progress = 0.99f;
            }

            // 범위 안전장치
            if (progress < 0f) progress = 0f;
            if (progress > 1f) progress = 1f;

            // 4. 아이콘 생성 및 배치
            ImageView ivTrain = new ImageView(getContext());
            ivTrain.setImageResource(R.drawable.ic_side_train);

            int w = (int) (40 * getResources().getDisplayMetrics().density);
            int h = (int) (32 * getResources().getDisplayMetrics().density);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
            params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
            ivTrain.setLayoutParams(params);

            // 위치 이동 (기차폭 만큼 보정)
            float moveDistance = (trackWidth - w) * progress-0.05f;

            // 역(점) 위에 섰을 때 기차 가운데가 점에 오도록 살짝 보정
            if (train.arvlCd.equals("1")) {
                moveDistance += (w * 0.2f); // 살짝 오른쪽으로 밀어줌
            }

            ivTrain.setTranslationX(moveDistance);
            container.addView(ivTrain);
        }
    }
}