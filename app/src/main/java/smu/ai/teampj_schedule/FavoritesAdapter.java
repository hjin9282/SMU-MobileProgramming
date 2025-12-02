package smu.ai.teampj_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<String> favoriteList; // 내부 저장용
    private final OnItemClickListener itemListener;
    private final OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onClick(String rawData);  // 텍스트 클릭
    }

    public interface OnDeleteClickListener {
        void onDelete(String rawData); // X 클릭
    }

    public FavoritesAdapter(List<String> list,
                            OnItemClickListener itemListener,
                            OnDeleteClickListener deleteListener) {
        this.favoriteList = list;
        this.itemListener = itemListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String raw = favoriteList.get(position);
        String[] parts = raw.split("\\|");

        String line = parts[0];
        String name = parts[1];
        // parts[2] = stationCode

        // 화면에 표시할 텍스트
        holder.tvFavoriteName.setText(line + " " + name + "역");

        // 텍스트 클릭 → 역 저장
        holder.itemView.setOnClickListener(v -> {
            if (itemListener != null) itemListener.onClick(raw);
        });

        // X 버튼 클릭 → 삭제
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(raw);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFavoriteName;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFavoriteName = itemView.findViewById(R.id.tvFavoriteName);
            btnDelete = itemView.findViewById(R.id.btnDeleteFavorite);
        }
    }
}
