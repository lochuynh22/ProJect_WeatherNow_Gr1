package com.re.weathernow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.re.weathernow.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    public interface OnHistoryClickListener {
        void onCityClick(String city);
        void onDeleteClick(String city);
    }

    private List<String> historyList;
    private OnHistoryClickListener listener;

    public SearchHistoryAdapter(List<String> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = historyList.get(position);

        holder.tvHistoryCity.setText(city);

        // Bấm vào tên thành phố → tìm kiếm luôn
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCityClick(city);
        });

        // Bấm ✕ → xóa khỏi lịch sử
        holder.tvDeleteHistory.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(city);
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public void updateList(List<String> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryCity;
        TextView tvDeleteHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryCity = itemView.findViewById(R.id.tvHistoryCity);
            tvDeleteHistory = itemView.findViewById(R.id.tvDeleteHistory);
        }
    }
}
