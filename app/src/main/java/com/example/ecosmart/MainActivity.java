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
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, complaints, notifications, logout;

    TextView tvWelcome, tvLastUpdated;
    TextView tvTotalCount, tvSafeCount, tvFullCount, tvHazardousCount;
    TextView tvHealthPercent, tvHealthLabel;
    ProgressBar progressBarHealth;

    CardView btnAllDustbins, btnFullDustbins, btnHazardous, btnComplaints;

    DatabaseReference dustbinRef;

    SessionManager sessionManager;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        currentUsername = sessionManager.getUsername();

        drawerLayout      = findViewById(R.id.drawerLayout);
        menu              = findViewById(R.id.menu);
        home              = findViewById(R.id.home);
        dustbins          = findViewById(R.id.dustbins);
        fullDustbins      = findViewById(R.id.full_dustbins);
        hazardousDustbins = findViewById(R.id.hazardous_dustbins);
        complaints        = findViewById(R.id.complaints);
        notifications     = findViewById(R.id.notification);
        logout            = findViewById(R.id.logout);


        tvWelcome         = findViewById(R.id.tv_welcome);
        tvLastUpdated     = findViewById(R.id.tv_last_updated);
        tvTotalCount      = findViewById(R.id.tv_total_count);
        tvSafeCount       = findViewById(R.id.tv_safe_count);
        tvFullCount       = findViewById(R.id.tv_full_count);
        tvHazardousCount  = findViewById(R.id.tv_hazardous_count);
        tvHealthPercent   = findViewById(R.id.tv_health_percent);
        tvHealthLabel     = findViewById(R.id.tv_health_label);
        progressBarHealth = findViewById(R.id.progressBar_health);

        btnAllDustbins  = findViewById(R.id.btn_all_dustbins);
        btnFullDustbins = findViewById(R.id.btn_full_dustbins);
        btnHazardous    = findViewById(R.id.btn_hazardous);
        btnComplaints   = findViewById(R.id.btn_complaints);

        String displayName = currentUsername.substring(0, 1).toUpperCase()
                + currentUsername.substring(1);
        tvWelcome.setText("Welcome back, " + displayName + "! ");

        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v -> recreate());
        dustbins.setOnClickListener(v ->
                redirectActivity(MainActivity.this, DustbinActivity.class));
        fullDustbins.setOnClickListener(v ->
                redirectActivity(MainActivity.this, FullDustbinsActivity.class));
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(MainActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v ->
                redirectActivity(MainActivity.this, ComplaintsActivity.class));
        notifications.setOnClickListener(v ->
                redirectActivity(MainActivity.this, NotificationActivity.class));
        logout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnAllDustbins.setOnClickListener(v ->
                redirectActivity(MainActivity.this, DustbinActivity.class));
        btnFullDustbins.setOnClickListener(v ->
                redirectActivity(MainActivity.this, FullDustbinsActivity.class));
        btnHazardous.setOnClickListener(v ->
                redirectActivity(MainActivity.this, HazardousActivity.class));
        btnComplaints.setOnClickListener(v ->
                redirectActivity(MainActivity.this, ComplaintsActivity.class));

        loadDashboardStats();
    }

    private void loadDashboardStats() {
        dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total      = 0;
                int full       = 0;
                int hazardous  = 0;
                int safe       = 0;

                for (DataSnapshot dustbinSnapshot : snapshot.getChildren()) {
                    Dustbin dustbin = dustbinSnapshot.getValue(Dustbin.class);
                    if (dustbin == null) continue;
                    total++;
                    if (dustbin.getFillLevel() >= 80) full++;
                    if ("Danger".equals(dustbin.getGasStatus())) hazardous++;
                }

                for (DataSnapshot dustbinSnapshot : snapshot.getChildren()) {
                    Dustbin dustbin = dustbinSnapshot.getValue(Dustbin.class);
                    if (dustbin == null) continue;

                    boolean isFull      = dustbin.getFillLevel() >= 80;
                    boolean isHazardous = "Danger".equals(dustbin.getGasStatus());
                    if (!isFull && !isHazardous) safe++;
                }
                tvTotalCount.setText(String.valueOf(total));
                tvSafeCount.setText(String.valueOf(safe));
                tvFullCount.setText(String.valueOf(full));
                tvHazardousCount.setText(String.valueOf(hazardous));

                int healthPercent = (total > 0)
                        ? Math.round((safe * 100f) / total) : 0;

                tvHealthPercent.setText(healthPercent + "%");
                progressBarHealth.setProgress(healthPercent);

                if (healthPercent >= 80) {
                    tvHealthLabel.setText("System is in great condition ");
                    tvHealthPercent.setTextColor(
                            android.graphics.Color.parseColor("#4CAF50"));
                    progressBarHealth.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#4CAF50")));
                } else if (healthPercent >= 50) {
                    tvHealthLabel.setText("Some dustbins need attention ");
                    tvHealthPercent.setTextColor(
                            android.graphics.Color.parseColor("#FF9800"));
                    progressBarHealth.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#FF9800")));
                } else {
                    tvHealthLabel.setText("Critical — immediate action required ");
                    tvHealthPercent.setTextColor(
                            android.graphics.Color.parseColor("#F44336"));
                    progressBarHealth.setProgressTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#F44336")));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String time = sdf.format(new Date());
                tvLastUpdated.setText("Last updated: " + time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Failed to load dashboard: " + error.getMessage(),
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