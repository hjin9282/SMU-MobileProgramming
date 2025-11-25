package smu.ai.teampj_schedule;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TimetableFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        SharedPreferences prefs =
                requireActivity().getSharedPreferences("subway", MODE_PRIVATE);

        String line = prefs.getString("line", "정보 없음");
        String station = prefs.getString("station", "정보 없음");

        TextView txt = view.findViewById(R.id.txtResult);
        txt.setText(line + " - " + station + " 시간표 표시 예정");

        return view;
    }
}
