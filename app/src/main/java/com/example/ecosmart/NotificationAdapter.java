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

public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context          = context;
        this.notificationList = notificationList;
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTime, tvMessage, tvLocation;
        CardView notificationCard;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType           = itemView.findViewById(R.id.tv_notification_type);
            tvTime           = itemView.findViewById(R.id.tv_notification_time);
            tvMessage        = itemView.findViewById(R.id.tv_notification_message);
            tvLocation       = itemView.findViewById(R.id.tv_notification_location);
            notificationCard = itemView.findViewById(R.id.notificationCard);
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NotificationViewHolder holder, int position) {

        Notification notification = notificationList.get(position);

        holder.tvMessage.setText(notification.getMessage());
        holder.tvLocation.setText(notification.getLocation());
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd MMM, hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(notification.getTimestamp()));
        holder.tvTime.setText(formattedDate);

        if ("Full".equals(notification.getType())) {
            holder.tvType.setText("Full");
            holder.tvType.setBackgroundColor(Color.parseColor("#FF5722"));
            holder.notificationCard.setCardBackgroundColor(
                    Color.parseColor("#FBF9FF"));
        } else if ("Hazardous".equals(notification.getType())) {
            holder.tvType.setText("Hazardous");
            holder.tvType.setBackgroundColor(Color.parseColor("#F44336"));
            holder.notificationCard.setCardBackgroundColor(
                    Color.parseColor("#FBF9FF"));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateList(List<Notification> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }
}