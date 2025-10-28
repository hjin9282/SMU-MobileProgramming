package smu.ai.transittime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BusDetailActivity extends AppCompatActivity {

    private TextView tvBusTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);

        tvBusTitle = (TextView) findViewById(R.id.tvBusTitle);

        // MainActivity의 Adapter가 보낸 Intent
        Intent intent = getIntent();

        // BUS_NUMBER_KEY 라는 이름표로 보낸 버스 번호(String)를 꺼냄 (기본값으로 정보 없음)
        String busNumber = intent.getStringExtra("BUS_NUMBER_KEY");

        tvBusTitle.setText(busNumber + "번 버스 노선도/위치");

        // ---
        // TODO: 여기에 나중에 네이버/카카오 맵 뷰(MapView)를 추가하고,
        //  이 busNumber(또는 API의 busId)로 API를 다시 호출해서 지도에 노선도와 실시간 위치 그리기
        // ---
    }
}