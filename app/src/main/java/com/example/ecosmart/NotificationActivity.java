package com.example.ecosmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, complaints, notifications, logout;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvNoNotifications, tvFullCount, tvHazardousCount;

    NotificationAdapter notificationAdapter;
    List<Notification> notificationList;
    DatabaseReference dustbinRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setupToolbar();

        drawerLayout      = findViewById(R.id.drawerLayout);
        menu              = findViewById(R.id.menu);
        home              = findViewById(R.id.home);
        dustbins          = findViewById(R.id.dustbins);
        fullDustbins      = findViewById(R.id.full_dustbins);
        hazardousDustbins = findViewById(R.id.hazardous_dustbins);
        complaints        = findViewById(R.id.complaints);
        notifications     = findViewById(R.id.notification);
        logout            = findViewById(R.id.logout);
        recyclerView      = findViewById(R.id.recyclerView_notifications);
        progressBar       = findViewById(R.id.progressBar_notifications);
        tvNoNotifications = findViewById(R.id.tv_no_notifications);
        tvFullCount       = findViewById(R.id.tv_full_count);
        tvHazardousCount  = findViewById(R.id.tv_hazardous_count);

        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v ->
                redirectActivity(NotificationActivity.this, MainActivity.class));
        dustbins.setOnClickListener(v ->
                redirectActivity(NotificationActivity.this, DustbinActivity.class));
        fullDustbins.setOnClickListener(v ->
                redirectActivity(NotificationActivity.this, FullDustbinsActivity.class));
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(NotificationActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v ->
                redirectActivity(NotificationActivity.this, ComplaintsActivity.class));
        notifications.setOnClickListener(v -> recreate());
        logout.setOnClickListener(v -> {
            new SessionManager(this).clearSession();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationAdapter);

        loadNotificationsFromDustbins();
    }

    private void loadNotificationsFromDustbins() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoNotifications.setVisibility(View.GONE);

        dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                int fullCount      = 0;
                int hazardousCount = 0;

                for (DataSnapshot dustbinSnapshot : snapshot.getChildren()) {
                    Dustbin dustbin = dustbinSnapshot.getValue(Dustbin.class);

                    if (dustbin == null) continue;
                    long timestamp = System.currentTimeMillis();
                    if (dustbin.getFillLevel() >= 80) {
                        fullCount++;
                        String message = "Dustbin at " + dustbin.getLocation()
                                + " is " + dustbin.getFillLevel()
                                + "% full. Immediate collection needed.";

                        Notification notification = new Notification(
                                dustbin.getId() + "_full",
                                dustbin.getId(),
                                dustbin.getLocation(),
                                "Full",
                                message,
                                timestamp
                        );
                        notificationList.add(notification);
                    }
                    if ("Danger".equals(dustbin.getGasStatus())) {
                        hazardousCount++;

                        String message = "Hazardous gas detected at "
                                + dustbin.getLocation()
                                + ". Immediate attention required!";

                        Notification notification = new Notification(
                                dustbin.getId() + "_hazardous",
                                dustbin.getId(),
                                dustbin.getLocation(),
                                "Hazardous",
                                message,
                                timestamp
                        );
                        notificationList.add(notification);
                    }
                }

                tvFullCount.setText(String.valueOf(fullCount));
                tvHazardousCount.setText(String.valueOf(hazardousCount));

                Collections.sort(notificationList, (n1, n2) -> {
                    if ("Hazardous".equals(n1.getType())
                            && "Full".equals(n2.getType())) return -1;
                    if ("Full".equals(n1.getType())
                            && "Hazardous".equals(n2.getType())) return 1;
                    return 0;
                });

                progressBar.setVisibility(View.GONE);

                if (notificationList.isEmpty()) {
                    tvNoNotifications.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoNotifications.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    notificationAdapter.updateList(notificationList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationActivity.this,
                        "Failed to load: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}