package com.re.weathernow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.re.weathernow.R;

import java.util.List;

public class CitySearchAdapter extends RecyclerView.Adapter<CitySearchAdapter.ViewHolder> {

    public interface OnSuggestionClickListener {
        void onCitySelected(String city);
    }

    private List<String> suggestions;
    private OnSuggestionClickListener listener;

    public CitySearchAdapter(List<String> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = suggestions.get(position);
        holder.tvSuggestionCity.setText(city);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCitySelected(city);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    public void updateSuggestions(List<String> newList) {
        this.suggestions = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSuggestionCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuggestionCity = itemView.findViewById(R.id.tvSuggestionCity);
        }
    }
}
