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
import java.util.List;

public class FullDustbinsActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, notifications, logout;
    LinearLayout complaints;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvEmpty, tvCount;

    DustbinAdapter dustbinAdapter;
    List<Dustbin> filteredList;
    DatabaseReference dustbinRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_dustbins);
        setupToolbar();

        drawerLayout      = findViewById(R.id.drawerLayout);
        menu              = findViewById(R.id.menu);
        home              = findViewById(R.id.home);
        dustbins          = findViewById(R.id.dustbins);
        fullDustbins      = findViewById(R.id.full_dustbins);
        hazardousDustbins = findViewById(R.id.hazardous_dustbins);
        complaints = findViewById(R.id.complaints);
        notifications     = findViewById(R.id.notification);
        logout            = findViewById(R.id.logout);
        recyclerView      = findViewById(R.id.recyclerView_full);
        progressBar       = findViewById(R.id.progressBar_loading);
        tvEmpty           = findViewById(R.id.tv_empty);
        tvCount           = findViewById(R.id.tv_count);

        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v ->
                redirectActivity(FullDustbinsActivity.this, MainActivity.class));
        dustbins.setOnClickListener(v ->
                redirectActivity(FullDustbinsActivity.this, DustbinActivity.class));
        fullDustbins.setOnClickListener(v -> recreate());
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(FullDustbinsActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v ->
                redirectActivity(FullDustbinsActivity.this, ComplaintsActivity.class));
        notifications.setOnClickListener(v ->
                redirectActivity(FullDustbinsActivity.this, NotificationActivity.class));
        logout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(FullDustbinsActivity.this);
            sessionManager.clearSession();

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FullDustbinsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        filteredList = new ArrayList<>();
        dustbinAdapter = new DustbinAdapter(this, filteredList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dustbinAdapter);

        loadFullDustbins();
    }

    private void loadFullDustbins() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filteredList.clear();

                for (DataSnapshot dustbinSnapshot : snapshot.getChildren()) {
                    Dustbin dustbin = dustbinSnapshot.getValue(Dustbin.class);
                    if (dustbin != null && dustbin.getFillLevel() >= 80) {
                        filteredList.add(dustbin);
                    }
                }

                progressBar.setVisibility(View.GONE);
                tvCount.setText(filteredList.size() + " found");

                if (filteredList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    dustbinAdapter.updateList(filteredList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FullDustbinsActivity.this,
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