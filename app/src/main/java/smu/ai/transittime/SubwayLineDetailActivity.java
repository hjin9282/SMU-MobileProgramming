package smu.ai.transittime;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SubwayLineDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subway_line_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // SubwayActivity가 보낸 Intent
        Intent intent = getIntent();

        // EXTRA_LINE_INFO 이름표로 보낸 데이터를 꺼냄 (기본값 -1 : 데이터 잘못 넘어왔는지 체크)
        int linePosition = intent.getIntExtra("EXTRA_LINE_INFO", -1);

        if (linePosition != -1) {

            setTitle("서울 지하철 " + (linePosition + 1) + "호선");

            // 이 position 값(0, 1, 2...)을 사용해서 0이면 1호선 데이터를 불러옴
            loadLineData(linePosition);

        } else {
            // 오류 처리
        }
    }

    private void loadLineData(int position) {
        // position 값에 따라 1호선, 2호선 등에 맞는 시간표, 노선도 데이터를 불러와서 화면 설정 여기에 구현
    }
}