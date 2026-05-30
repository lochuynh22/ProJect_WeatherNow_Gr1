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

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private List<ForecastResponse.ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastResponse.ForecastItem> forecastList) {
        this.forecastList = forecastList;
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
        holder.tvDate.setText(item.dateTime);
        holder.tvForecastTemp.setText(Math.round(item.main.temp) + "°C");
        holder.tvForecastDesc.setText(item.weather.get(0).description);

        String iconUrl = "https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png";
        Glide.with(holder.itemView.getContext()).load(iconUrl).into(holder.imgIcon);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvForecastTemp, tvForecastDesc;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvForecastTemp = itemView.findViewById(R.id.tvForecastTemp);
            tvForecastDesc = itemView.findViewById(R.id.tvForecastDesc);
            imgIcon = itemView.findViewById(R.id.imgForecastIcon);
        }
    }
}
