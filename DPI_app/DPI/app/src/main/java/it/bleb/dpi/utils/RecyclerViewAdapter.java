package it.bleb.dpi.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.bleb.dpi.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<DpiDetails> dpiDetailsList;
    private Context context;

    public RecyclerViewAdapter(List<DpiDetails> dpiDetailsList, Context context) {
        this.dpiDetailsList = dpiDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_dpi, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DpiDetails dpiDetail = dpiDetailsList.get(position);
        holder.id.setText(dpiDetail.getAddress());
        holder.nome.setText(dpiDetail.getName());
        int lvl = dpiDetail.getBatteryLvl();
        String sLvl = null;
        if (lvl < 0) {
            sLvl = "--";
            //holder.batteryLvl.setTextSize(8.5f);
        } else {
            sLvl = String.valueOf(lvl) + "%";
            holder.batteryLvl.setTextSize(12.0f);
        }
        holder.batteryLvl.setText(sLvl);
        holder.battery.setImageResource(setBatteryIcon(lvl));
    }

    private int setBatteryIcon(int batteryLvl) {
        int batteryIc = -1;
        if (batteryLvl <= 100 && batteryLvl >= 86) {
            batteryIc = R.drawable.ic_battery_86_100;
        } else if (batteryLvl < 86 && batteryLvl >= 71) {
            batteryIc = R.drawable.ic_battery_71_85;
        } else if (batteryLvl < 71 && batteryLvl >= 36) {
            batteryIc = R.drawable.ic_battery_36_70;
        } else if (batteryLvl < 36 && batteryLvl >= 5) {
            batteryIc = R.drawable.ic_battery_5_35;
        } else if (batteryLvl < 5) {
            batteryIc = R.drawable.ic_battery_0_4;
        }
        return batteryIc;
    }

    @Override
    public int getItemCount() {
        return dpiDetailsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView id, nome, batteryLvl;
        private ImageView battery;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.dpi_id);
            nome = itemView.findViewById(R.id.dpi_name);
            batteryLvl = itemView.findViewById(R.id.dpi_battery_lvl);
            battery = itemView.findViewById(R.id.dpi_battery);
        }
    }
}
