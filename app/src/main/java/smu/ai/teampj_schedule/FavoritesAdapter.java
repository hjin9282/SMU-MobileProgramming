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

    private List<String> list;
    private OnItemClickListener itemListener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onClick(String item);  // 텍스트 클릭
    }

    public interface OnDeleteClickListener {
        void onDelete(String item); // X 클릭
    }

    public FavoritesAdapter(List<String> list,
                            OnItemClickListener itemListener,
                            OnDeleteClickListener deleteListener) {
        this.list = list;
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
        String item = list.get(position);
        holder.tvFavoriteName.setText(item);

        // 텍스트 클릭 → 역 저장
        holder.tvFavoriteName.setOnClickListener(v -> {
            itemListener.onClick(item);
        });

        // X 버튼 클릭 → 삭제
        holder.btnDelete.setOnClickListener(v -> {
            deleteListener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
