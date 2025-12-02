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

    public interface OnDeleteClickListener {
        void onDelete(String item);
    }

    private final List<String> list;
    private final OnDeleteClickListener deleteListener;

    public FavoritesAdapter(List<String> list, OnDeleteClickListener deleteListener) {
        this.list = list;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = list.get(position);
        holder.tvName.setText(item);

        holder.btnDelete.setOnClickListener(v -> {
            deleteListener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvFavoriteName);
            btnDelete = itemView.findViewById(R.id.btnDeleteFavorite);
        }
    }
}
