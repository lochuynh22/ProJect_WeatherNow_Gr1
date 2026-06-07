package com.re.weathernow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.re.weathernow.R;
import com.re.weathernow.model.ForecastResponse;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {

    private List<ForecastResponse.ForecastItem> items;
    // Fix 3: Thêm unit để hiển thị đúng °C hoặc °F
    private String unit;

    public HourlyAdapter(List<ForecastResponse.ForecastItem> items, String unit) {
        this.items = items;
        this.unit = unit;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = items.get(position);

        // Lấy giờ từ item.dateTime (format "yyyy-MM-dd HH:mm:ss"), lấy ký tự 11-16
        if (item.dateTime != null && item.dateTime.length() >= 16) {
            holder.tvHourTime.setText(item.dateTime.substring(11, 16));
        } else {
            holder.tvHourTime.setText("--:--");
        }

        // Fix 4: Null-check cho weather list trước khi load icon
        if (item.weather != null && !item.weather.isEmpty()) {
            String icon = item.weather.get(0).icon;
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
            Glide.with(holder.imgHourIcon.getContext())
                    .load(iconUrl)
                    .into(holder.imgHourIcon);
        }

        // Fix 3: Hiển thị nhiệt độ kèm đơn vị đúng (°C hoặc °F)
        if (item.main != null) {
            String tempUnit = (unit != null && unit.equals("imperial")) ? "°F" : "°C";
            holder.tvHourTemp.setText(Math.round(item.main.temp) + tempUnit);
        } else {
            holder.tvHourTemp.setText("--");
        }

        // Xác suất mưa
        if (item.pop == 0f) {
            holder.tvHourPop.setVisibility(View.GONE);
        } else {
            holder.tvHourPop.setVisibility(View.VISIBLE);
            holder.tvHourPop.setText(Math.round(item.pop * 100) + "%");
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Fix 3: setItems cũng nhận unit để cập nhật khi người dùng toggle đơn vị
    public void setItems(List<ForecastResponse.ForecastItem> items, String unit) {
        this.items = items;
        this.unit = unit;
        notifyDataSetChanged();
    }

    public static class HourlyViewHolder extends RecyclerView.ViewHolder {

        TextView tvHourTime;
        ImageView imgHourIcon;
        TextView tvHourTemp;
        TextView tvHourPop;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHourTime = itemView.findViewById(R.id.tvHourTime);
            imgHourIcon = itemView.findViewById(R.id.imgHourIcon);
            tvHourTemp = itemView.findViewById(R.id.tvHourTemp);
            tvHourPop = itemView.findViewById(R.id.tvHourPop);
        }
    }
}
