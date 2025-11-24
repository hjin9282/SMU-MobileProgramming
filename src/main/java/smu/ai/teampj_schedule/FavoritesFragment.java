package smu.ai.teampj_schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private ArrayList<String> favoriteList;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        ListView listView = view.findViewById(R.id.favoriteList);
        Button btnAdd = view.findViewById(R.id.btnAdd);

        favoriteList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), R.layout.item_favorite, favoriteList);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            var line = PreferenceManager.getLine(getContext());
            var station = PreferenceManager.getStation(getContext());
            favoriteList.add(line+" "+station);
            adapter.notifyDataSetChanged();
        });

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            favoriteList.remove(position);
            adapter.notifyDataSetChanged();
            return true;
        });

        return view;
    }
}
