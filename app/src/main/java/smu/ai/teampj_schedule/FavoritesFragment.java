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
    private List<String> favoriteList;
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

        favoriteList = PreferenceManager.getFavorites(getContext());

        adapter = new FavoritesAdapter(
                favoriteList,

                // 텍스트 클릭 → 역 선택 + station_info 저장
                item -> {
                    String[] parts = item.split(" ");
                    String line = parts[0];
                    String stationName = parts[1];
                    stationName = stationName.replace("역", "").trim();

                    String stationCode = StationUtils.findStationCode(
                            getContext(), line, stationName
                    );

                    PreferenceManager.saveStationInfo(
                            getContext(),
                            stationName, line, stationCode
                    );

                    Toast.makeText(getContext(), item + " 선택됨", Toast.LENGTH_SHORT).show();
                },

                // X 버튼 클릭 → 즐겨찾기 삭제
                item -> {
                    PreferenceManager.removeFavorite(getContext(), item);
                    loadFavorites();
                    Toast.makeText(getContext(), item + " 삭제됨", Toast.LENGTH_SHORT).show();
                }
        );

        favoriteRecycler.setAdapter(adapter);
    }


    // 홈 화면에서 저장된 현재 역 정보 → 즐겨찾기에 추가
    private void addFavoriteFromHome() {
        String station = PreferenceManager.getStation(getContext());
        String stationLine = PreferenceManager.getLine(getContext());

        if (station == null || station.trim().isEmpty()) {
            Toast.makeText(getContext(), "홈 화면에서 역을 먼저 검색하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        PreferenceManager.addFavorite(getContext(), station, stationLine);

        Toast.makeText(getContext(), stationLine + " " + station + " 즐겨찾기 추가됨", Toast.LENGTH_SHORT).show();
        loadFavorites();
    }
}
