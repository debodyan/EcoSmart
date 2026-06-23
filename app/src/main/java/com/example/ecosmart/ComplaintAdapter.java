package com.example.ecosmart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private Context context;
    private List<Complaint> complaintList;

    public ComplaintAdapter(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvStatus, tvLocation, tvDescription, tvTime;
        CardView complaintCard;
        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername    = itemView.findViewById(R.id.tv_complaint_username);
            tvStatus      = itemView.findViewById(R.id.tv_complaint_status);
            tvLocation    = itemView.findViewById(R.id.tv_complaint_location);
            tvDescription = itemView.findViewById(R.id.tv_complaint_description);
            tvTime        = itemView.findViewById(R.id.tv_complaint_time);
            complaintCard = itemView.findViewById(R.id.complaintCard);
        }
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.tvUsername.setText("@" + complaint.getUsername());
        holder.tvLocation.setText(complaint.getLocation());
        holder.tvDescription.setText(complaint.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(complaint.getTimestamp()));
        holder.tvTime.setText("Submitted: " + formattedDate);

        holder.tvStatus.setText(complaint.getStatus());
        if ("Resolved".equals(complaint.getStatus())) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_safe);
            holder.complaintCard.setCardBackgroundColor(
                    Color.parseColor("#F1FFF1"));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_danger);
            holder.complaintCard.setCardBackgroundColor(
                    Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void updateList(List<Complaint> newList) {
        this.complaintList = newList;
        notifyDataSetChanged();
    }
}