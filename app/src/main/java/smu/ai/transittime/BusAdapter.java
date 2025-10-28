package smu.ai.transittime;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private List<BusInfo> items;

    // 생성자에서 데이터 리스트 받음
    public BusAdapter(List<BusInfo> items) {
        this.items = items;
    }

    // ViewHolder: 'list_item_bus.xml'의 뷰들을 보관하는 객체 (static inner class)
    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView busNumber, remainingTime, currentLocation;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumber = itemView.findViewById(R.id.tvBusNumber);
            remainingTime = itemView.findViewById(R.id.tvRemainingTime);
            currentLocation = itemView.findViewById(R.id.tvCurrentLocation);
        }
    }

    // onCreateViewHolder: ViewHolder를 생성하고 레이아웃을 붙이는 곳
    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bus, parent, false);
        return new BusViewHolder(view);
    }

    // onBindViewHolder: ViewHolder에 실제 데이터를 바인딩(연결)하는 곳
    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        BusInfo item = items.get(position); // 현재 위치의 데이터

        // 뷰에 데이터를 설정합니다.
        holder.busNumber.setText(item.getNumber());
        holder.remainingTime.setText(item.getTime());
        holder.currentLocation.setText(item.getLocation());

        // holder.itemView 는 각 버스 항목의 가장 바깥쪽 레이아웃(LinearLayout)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시, 해당 항목(View)의 Context를 가져옵니다.
                Context context = holder.itemView.getContext();

                Intent intent = new Intent(context, BusDetailActivity.class);

                // BUS_NUMBER_KEY 라는 이름표로 실제 버스 번호(item.getNumber())를 붙임
                intent.putExtra("BUS_NUMBER_KEY", item.getNumber());

                // (나중에 API에서 버스 노선 ID(예: "100100018")를 받으면, 그 ID를 넘기는 게 더 정확 )
                // intent.putExtra("BUS_ID_KEY", item.getBusRouteId());

                context.startActivity(intent);
            }
        });
    }

    // getItemCount: 목록의 총 개수
    @Override
    public int getItemCount() {
        return items.size();
    }
}
