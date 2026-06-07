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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private List<ForecastResponse.ForecastItem> forecastList;
    private String unit; // "metric" → °C | "imperial" → °F

    public ForecastAdapter(List<ForecastResponse.ForecastItem> forecastList, String unit) {
        this.forecastList = forecastList;
        this.unit = unit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = forecastList.get(position);

        // Fix 2: Format ngày đẹp "Thứ 6, 07/06" thay vì raw "2024-06-07 12:00:00"
        holder.tvDate.setText(formatDate(item.dateTime));

        // Đơn vị nhiệt độ
        String tempUnit = (unit != null && unit.equals("imperial")) ? "°F" : "°C";

        // Fix 4: Null-check cho main trước khi truy cập
        if (item.main != null) {
            holder.tvForecastTemp.setText(Math.round(item.main.temp) + tempUnit);
            holder.tvForecastHumidity.setText("💧 " + item.main.humidity + "%");
            // Min ~ Max kèm đơn vị
            holder.tvForecastMinMax.setText(
                Math.round(item.main.tempMin) + "~" + Math.round(item.main.tempMax) + tempUnit
            );
        } else {
            holder.tvForecastTemp.setText("--" + tempUnit);
            holder.tvForecastHumidity.setText("💧 --%");
            holder.tvForecastMinMax.setText("--");
        }

        // Fix 4: Null-check cho weather list trước khi truy cập
        if (item.weather != null && !item.weather.isEmpty()) {
            holder.tvForecastDesc.setText(item.weather.get(0).description);
            String iconUrl = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png";
            Glide.with(holder.itemView.getContext()).load(iconUrl).into(holder.imgIcon);
        } else {
            holder.tvForecastDesc.setText("");
        }

        // Xác suất mưa
        int popPercent = Math.round(item.pop * 100);
        holder.tvForecastPop.setText("🌧 " + popPercent + "%");
    }

    /**
     * Fix 2: Chuyển "2024-06-07 12:00:00" → "Thứ 6, 07/06"
     */
    private String formatDate(String rawDateTime) {
        if (rawDateTime == null || rawDateTime.length() < 10) return "--";
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputSdf.parse(rawDateTime);
            if (date == null) return rawDateTime.substring(0, 10);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // Lấy tên ngày trong tuần bằng tiếng Việt
            String[] dayNames = {"CN", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=CN, 1=T2...

            // Định dạng ngày/tháng
            SimpleDateFormat outputSdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return dayNames[dayOfWeek] + ", " + outputSdf.format(date);
        } catch (ParseException e) {
            // Fallback: trả về "dd/MM" nếu parse lỗi
            return rawDateTime.substring(8, 10) + "/" + rawDateTime.substring(5, 7);
        }
    }

    @Override
    public int getItemCount() {
        return forecastList != null ? forecastList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvForecastTemp, tvForecastDesc;
        TextView tvForecastHumidity, tvForecastPop, tvForecastMinMax;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvForecastTemp = itemView.findViewById(R.id.tvForecastTemp);
            tvForecastDesc = itemView.findViewById(R.id.tvForecastDesc);
            imgIcon = itemView.findViewById(R.id.imgForecastIcon);
            tvForecastHumidity = itemView.findViewById(R.id.tvForecastHumidity);
            tvForecastPop = itemView.findViewById(R.id.tvForecastPop);
            tvForecastMinMax = itemView.findViewById(R.id.tvForecastMinMax);
        }
    }
}

