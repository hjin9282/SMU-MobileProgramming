package smu.ai.teampj_schedule;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import smu.ai.teampj_schedule.model.TimeItem;

public class TimeTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items = new ArrayList<>();
    private int highlightIndex = -1;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;

    public void setItems(List<Object> list, int highlightIndex) {
        this.items = list;
        this.highlightIndex = highlightIndex;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? TYPE_HEADER : TYPE_ROW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_time_header, parent, false);
            return new HourViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_time_row, parent, false);
            return new RowViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HourViewHolder) {
            String hour = (String) items.get(position);
            ((HourViewHolder) holder).txtHour.setText(hour + "시");

        } else if (holder instanceof RowViewHolder) {
            TimeItem item = (TimeItem) items.get(position);
            RowViewHolder h = (RowViewHolder) holder;

            h.txtTime.setText(item.time);
            h.txtDestination.setText(item.dest + "행");

            if (position == highlightIndex) {
                h.itemView.setBackgroundColor(Color.parseColor("#332196F3"));
                h.txtTime.setTypeface(null, Typeface.BOLD);
                h.txtDestination.setTypeface(null, Typeface.BOLD);
            } else {
                h.itemView.setBackgroundColor(Color.TRANSPARENT);
                h.txtTime.setTypeface(null, Typeface.NORMAL);
                h.txtDestination.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    // 시간 헤더
    static class HourViewHolder extends RecyclerView.ViewHolder {
        TextView txtHour;

        public HourViewHolder(View itemView) {
            super(itemView);
            txtHour = itemView.findViewById(R.id.txtHour);
        }
    }

    // 열차 리스트
    static class RowViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtDestination, labelExpress;

        public RowViewHolder(View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDestination = itemView.findViewById(R.id.txtDestination);
            labelExpress = itemView.findViewById(R.id.labelExpress);
        }
    }
}
