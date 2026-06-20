package com.example.ecosmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import java.util.List;

public class DustbinActivity extends BaseActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, complaints, notifications, logout;
    EditText etSearch;
    ImageView ivClearSearch;
    TextView tvResultsInfo, tvResultsCount, tvNoResultsQuery;
    LinearLayout layoutNoResults;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvEmpty;

    DustbinAdapter dustbinAdapter;
    List<Dustbin> dustbinList;
    DatabaseReference dustbinRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbins);
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
        etSearch        = findViewById(R.id.et_search);
        ivClearSearch   = findViewById(R.id.iv_clear_search);
        tvResultsInfo   = findViewById(R.id.tv_results_info);
        tvResultsCount  = findViewById(R.id.tv_results_count);
        tvNoResultsQuery = findViewById(R.id.tv_no_results_query);
        layoutNoResults = findViewById(R.id.layout_no_results);
        recyclerView = findViewById(R.id.recyclerView_dustbins);
        progressBar  = findViewById(R.id.progressBar_loading);
        tvEmpty      = findViewById(R.id.tv_empty);


        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v ->
                redirectActivity(DustbinActivity.this, MainActivity.class));
        dustbins.setOnClickListener(v -> recreate());
        fullDustbins.setOnClickListener(v ->
                redirectActivity(DustbinActivity.this, FullDustbinsActivity.class));
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(DustbinActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v ->
                redirectActivity(DustbinActivity.this, ComplaintsActivity.class));
        notifications.setOnClickListener(v ->
                redirectActivity(DustbinActivity.this, NotificationActivity.class));
        logout.setOnClickListener(v -> {
            new SessionManager(this).clearSession();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DustbinActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dustbinList    = new ArrayList<>();
        dustbinAdapter = new DustbinAdapter(this, dustbinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dustbinAdapter);

        setupSearch();
        loadDustbinsFromFirebase();
    }

    private void setupSearch() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                String query = s.toString();
                ivClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                dustbinAdapter.filter(query);
                updateResultsInfo(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ivClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.clearFocus();
        });
    }

    private void updateResultsInfo(String query) {
        int filtered = dustbinAdapter.getFilteredCount();
        int total    = dustbinAdapter.getTotalCount();

        if (query == null || query.trim().isEmpty()) {
            tvResultsInfo.setText("All Dustbins");
            tvResultsCount.setText(total + " found");
            layoutNoResults.setVisibility(View.GONE);
            if (total > 0) {
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else if (filtered == 0) {
            tvResultsInfo.setText("No results");
            tvResultsCount.setText("");
            recyclerView.setVisibility(View.GONE);
            layoutNoResults.setVisibility(View.VISIBLE);
            tvNoResultsQuery.setText("No results for \"" + query + "\"");

        } else {
            tvResultsInfo.setText("Search results");
            tvResultsCount.setText(filtered + " of " + total + " dustbins");
            layoutNoResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadDustbinsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dustbinList.clear();
                for (DataSnapshot dustbinSnapshot : snapshot.getChildren()) {
                    Dustbin dustbin = dustbinSnapshot.getValue(Dustbin.class);
                    if (dustbin != null) dustbinList.add(dustbin);
                }
                progressBar.setVisibility(View.GONE);
                if (dustbinList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    dustbinAdapter.updateList(dustbinList);
                    String currentQuery = etSearch.getText().toString();
                    if (!currentQuery.isEmpty()) {
                        dustbinAdapter.filter(currentQuery);
                    }
                    updateResultsInfo(currentQuery);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DustbinActivity.this,
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