package smu.ai.teampj_schedule;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoriteRecycler;
    private FavoritesAdapter adapter;
    private List<String> favoriteList; // 내부 저장용
    private Button btnAddFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoriteRecycler = view.findViewById(R.id.favoriteRecycler);
        favoriteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddFavorite = view.findViewById(R.id.btnAddFavorite);

        btnAddFavorite.setOnClickListener(v -> addFavoriteFromHome());

        loadFavorites();

        return view;
    }

    private void loadFavorites() {

        // 4호선|숙대입구|415 형태의 raw data
        favoriteList = PreferenceManager.getFavorites(getContext());

        adapter = new FavoritesAdapter(
                favoriteList,

                // 텍스트 클릭 → 역 선택 + station_info 저장
                item -> {
                    // item: 4호선|숙대입구|415
                    String[] parts = item.split("\\|");
                    if (parts.length < 3) {
                        Toast.makeText(getContext(), "즐겨찾기 데이터 오류", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String line = parts[0];
                    String stationName = parts[1];
                    String stationCode = parts[2];

                    // 선택된 역 정보를 전역으로 저장
                    PreferenceManager.saveStationInfo(
                            getContext(),
                            stationName,
                            line,
                            stationCode
                    );

                    Toast.makeText(getContext(), line + " " + stationName + "역 선택됨", Toast.LENGTH_SHORT).show();
                },

                // X 버튼 클릭 → 즐겨찾기 삭제
                item -> {
                    // raw → UI 텍스트 변환
                    String[] parts = item.split("\\|");
                    String line = parts[0];
                    String name = parts[1];

                    String display = line + " " + name + "역";

                    PreferenceManager.removeFavorite(getContext(), item);
                    loadFavorites();

                    Toast.makeText(getContext(), display + " 삭제됨", Toast.LENGTH_SHORT).show();
                }
        );

        favoriteRecycler.setAdapter(adapter);
    }

    // 홈 화면에서 저장된 현재 역 정보 → 즐겨찾기에 추가
    private void addFavoriteFromHome() {
        String stationDisplay = PreferenceManager.getStation(getContext());
        String stationLine = PreferenceManager.getLine(getContext());
        String stationCode = PreferenceManager.getStationCode(getContext());

        if (stationDisplay == null || stationDisplay.trim().isEmpty()) {
            Toast.makeText(getContext(), "홈 화면에서 역을 먼저 검색하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stationCode == null || stationCode.trim().isEmpty()) {
            Toast.makeText(getContext(), "현재 역 코드 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // "숙대입구역" → "숙대입구"
        String stationName = stationDisplay;
        if (stationName.endsWith("역")) {
            stationName = stationName.substring(0, stationName.length() - 1);
        }
        stationName = stationName.trim();

        PreferenceManager.addFavorite(
                getContext(),
                stationName,
                stationLine,
                stationCode
        );

        Toast.makeText(
                getContext(),
                stationLine + " " + stationName + "역 즐겨찾기 추가됨",
                Toast.LENGTH_SHORT
        ).show();

        loadFavorites();
    }
}
