package com.example.ecosmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ComplaintsActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, complaints, notifications, logout;
    EditText etLocation, etDescription;
    Button btnSubmit;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvNoComplaints;

    ComplaintAdapter complaintAdapter;
    List<Complaint> complaintList;
    DatabaseReference complaintsRef;

    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        setupToolbar();
        SessionManager sessionManager = new SessionManager(this);
        currentUsername = sessionManager.getUsername();

        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        drawerLayout      = findViewById(R.id.drawerLayout);
        menu              = findViewById(R.id.menu);
        home              = findViewById(R.id.home);
        dustbins          = findViewById(R.id.dustbins);
        fullDustbins      = findViewById(R.id.full_dustbins);
        hazardousDustbins = findViewById(R.id.hazardous_dustbins);
        complaints        = findViewById(R.id.complaints);
        notifications     = findViewById(R.id.notification);
        logout            = findViewById(R.id.logout);
        etLocation        = findViewById(R.id.et_complaint_location);
        etDescription     = findViewById(R.id.et_complaint_description);
        btnSubmit         = findViewById(R.id.btn_submit_complaint);
        recyclerView      = findViewById(R.id.recyclerView_complaints);
        progressBar       = findViewById(R.id.progressBar_complaints);
        tvNoComplaints    = findViewById(R.id.tv_no_complaints);

        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v ->
                redirectActivity(ComplaintsActivity.this, MainActivity.class));
        dustbins.setOnClickListener(v ->
                redirectActivity(ComplaintsActivity.this, DustbinActivity.class));
        fullDustbins.setOnClickListener(v ->
                redirectActivity(ComplaintsActivity.this, FullDustbinsActivity.class));
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(ComplaintsActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v -> recreate());
        notifications.setOnClickListener(v ->
                redirectActivity(ComplaintsActivity.this, NotificationActivity.class));
        logout.setOnClickListener(v -> {
            SessionManager sessionManagerr = new SessionManager(ComplaintsActivity.this);
            sessionManagerr.clearSession();

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ComplaintsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(this, complaintList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(complaintAdapter);

        btnSubmit.setOnClickListener(v -> submitComplaint());

        loadMyComplaints();
    }

    private void submitComplaint() {
        String location    = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (location.isEmpty()) {
            etLocation.setError("Please enter a location");
            etLocation.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Please describe the issue");
            etDescription.requestFocus();
            return;
        }
        complaintsRef = FirebaseDatabase.getInstance().getReference("Complaints");
        String complaintId = complaintsRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        Complaint complaint = new Complaint(
                complaintId,
                currentUsername,
                location,
                description,
                "Pending",
                timestamp
        );

        complaintsRef.child(complaintId).setValue(complaint)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ComplaintsActivity.this,
                            "Complaint submitted successfully!",
                            Toast.LENGTH_SHORT).show();
                    etLocation.setText("");
                    etDescription.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ComplaintsActivity.this,
                            "Failed to submit: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void loadMyComplaints() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoComplaints.setVisibility(View.GONE);

        complaintsRef = FirebaseDatabase.getInstance().getReference("Complaints");

        complaintsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                complaintList.clear();

                for (DataSnapshot complaintSnapshot : snapshot.getChildren()) {
                    Complaint complaint = complaintSnapshot.getValue(Complaint.class);

                    if (complaint != null &&
                            currentUsername.equals(complaint.getUsername())) {
                        complaintList.add(complaint);
                    }
                }

                Collections.sort(complaintList,
                        (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                progressBar.setVisibility(View.GONE);

                if (complaintList.isEmpty()) {
                    tvNoComplaints.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoComplaints.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    complaintAdapter.updateList(complaintList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ComplaintsActivity.this,
                        "Failed to load complaints: " + error.getMessage(),
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