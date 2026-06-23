package com.example.ecosmart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class DustbinAdapter extends RecyclerView.Adapter<DustbinAdapter.DustbinViewHolder> {

    private Context context;
    private List<Dustbin> fullList;
    private List<Dustbin> filteredList;


    public DustbinAdapter(Context context, List<Dustbin> dustbinList) {
        this.context      = context;
        this.fullList     = new ArrayList<>(dustbinList);
        this.filteredList = new ArrayList<>(dustbinList);
    }

    public static class DustbinViewHolder extends RecyclerView.ViewHolder {
        TextView tvDustbinId, tvLocation, tvGasStatus, tvFillPercent;
        ProgressBar progressBarFill;
        CardView dustbinCard;

        public DustbinViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDustbinId     = itemView.findViewById(R.id.tv_dustbin_id);
            tvLocation      = itemView.findViewById(R.id.tv_location);
            tvGasStatus     = itemView.findViewById(R.id.tv_gas_status);
            tvFillPercent   = itemView.findViewById(R.id.tv_fill_percent);
            progressBarFill = itemView.findViewById(R.id.progressBar_fill);
            dustbinCard     = itemView.findViewById(R.id.dustbinCard);
        }
    }

    @NonNull
    @Override
    public DustbinViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dustbin, parent, false);
        return new DustbinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DustbinViewHolder holder, int position) {

        Dustbin dustbin = filteredList.get(position);
        holder.tvDustbinId.setText(dustbin.getId());
        holder.tvLocation.setText(dustbin.getLocation());
        holder.tvFillPercent.setText(dustbin.getFillLevel() + "%");
        holder.progressBarFill.setProgress(dustbin.getFillLevel());

        if (dustbin.getFillLevel() >= 80) {
            holder.progressBarFill.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#F44336")));
        } else if (dustbin.getFillLevel() >= 50) {
            holder.progressBarFill.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#FF9800")));
        } else {
            holder.progressBarFill.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#4CAF50")));
        }

        holder.tvGasStatus.setText(dustbin.getGasStatus());
        if ("Danger".equals(dustbin.getGasStatus())) {
            holder.tvGasStatus.setBackgroundResource(R.drawable.bg_status_danger);
        } else {
            holder.tvGasStatus.setBackgroundResource(R.drawable.bg_status_safe);
        }

        if (dustbin.getFillLevel() >= 80
                || "Danger".equals(dustbin.getGasStatus())) {
            holder.dustbinCard.setCardBackgroundColor(
                    Color.parseColor("#FBF9FF"));
        } else {
            holder.dustbinCard.setCardBackgroundColor(
                    Color.parseColor("#FBF9FF"));
        }

        LinearLayout btnViewMap = holder.itemView.findViewById(R.id.btn_view_map);
        btnViewMap.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("dustbinId", dustbin.getId());
            intent.putExtra("location", dustbin.getLocation());
            intent.putExtra("latitude", dustbin.getLatitude());
            intent.putExtra("longitude", dustbin.getLongitude());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    public void updateList(List<Dustbin> newList) {
        this.fullList     = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    public int getFilteredCount() {
        return filteredList.size();
    }
    public int getTotalCount() {
        return fullList.size();
    }

    public void filter(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);

        } else {
            String lowerQuery = query.toLowerCase().trim();

            for (Dustbin dustbin : fullList) {

                boolean matchesId = dustbin.getId().toLowerCase().contains(lowerQuery);
                boolean matchesLocation = dustbin.getLocation().toLowerCase().contains(lowerQuery);
                boolean matchesGas = dustbin.getGasStatus().toLowerCase().contains(lowerQuery);
                boolean matchesFill = String.valueOf(dustbin.getFillLevel()).contains(lowerQuery);

                if (matchesId || matchesLocation || matchesGas || matchesFill) {
                    filteredList.add(dustbin);
                }
            }
        }

        notifyDataSetChanged();
    }
}
