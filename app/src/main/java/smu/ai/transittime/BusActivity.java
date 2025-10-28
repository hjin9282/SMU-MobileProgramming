package smu.ai.transittime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // RecyclerView Import

import java.util.ArrayList;
import java.util.List;

public class BusActivity extends AppCompatActivity {

    private Button btnRefresh;
    private RecyclerView recyclerViewBus;
    private SearchView searchVal;

    private BusAdapter busAdapter;
    private List<BusInfo> busList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        recyclerViewBus = (RecyclerView) findViewById(R.id.recyclerViewBus);
        searchVal = (SearchView) findViewById(R.id.searchVal);

        // RecyclerView 설정
        setupRecyclerView();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBusData();
            }
        });

        searchVal.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 입력 글자가 바뀔 때
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            // 검색 버튼 눌렀을 때
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        loadBusData();
    }

    private void setupRecyclerView() {
        busAdapter = new BusAdapter(busList);

        recyclerViewBus.setAdapter(busAdapter);
        recyclerViewBus.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadBusData() {
        // ---
        // TODO: 여기에 Retrofit2 API 호출 코드가 들어가야 함
        // ---

        // 데모용 임시 데이터를 생성
        busList.clear(); // 기존 목록 삭제

        // 객체 생성
        busList.add(new BusInfo("143", "[5분 20초]", "3정거장 전 (대학로)"));
        busList.add(new BusInfo("100", "[8분 05초]", "5정거장 전 (서울역)"));
        busList.add(new BusInfo("7016", "[12분 40초]", "7정거장 전 (시청)"));
        busList.add(new BusInfo("470", "[1분 30초]", "1정거장 전 (혜화역)"));
        busList.add(new BusInfo("272", "[도착 예정]", "곧 도착"));

        // 어댑터에게 데이터가 변경되었음을 알림 (UI 갱신)
        busAdapter.notifyDataSetChanged();
    }
}