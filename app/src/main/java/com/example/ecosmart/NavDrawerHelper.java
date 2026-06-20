package com.example.ecosmart;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavDrawerHelper {

    private Activity activity;
    private SessionManager sessionManager;

    public NavDrawerHelper(Activity activity) {
        this.activity       = activity;
        this.sessionManager = new SessionManager(activity);
    }

    public void setupNavDrawer() {
        loadUserInfo();
        loadBadgeCounts();
        setupNavProfileRow();
    }

    private void loadUserInfo() {
        String username = sessionManager.getUsername();
        if (username == null) return;

        TextView navUsername = activity.findViewById(R.id.nav_username);
        if (navUsername != null) navUsername.setText("@" + username);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name     = snapshot.child("name").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);

                TextView navName = activity.findViewById(R.id.nav_user_name);
                if (navName != null && name != null) {
                    navName.setText(name);
                }
                ImageView navPhoto = activity.findViewById(R.id.nav_profile_photo);
                if (navPhoto != null && photoUrl != null && !photoUrl.isEmpty()) {
                    ProfileActivity.loadBase64Image(photoUrl, navPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadBadgeCounts() {
        DatabaseReference dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int fullCount      = 0;
                int hazardousCount = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Dustbin dustbin = child.getValue(Dustbin.class);
                    if (dustbin == null) continue;
                    if (dustbin.getFillLevel() >= 80) fullCount++;
                    if ("Danger".equals(dustbin.getGasStatus())) hazardousCount++;
                }

                TextView badgeFull = activity.findViewById(R.id.nav_badge_full);
                if (badgeFull != null) {
                    if (fullCount > 0) {
                        badgeFull.setText(String.valueOf(fullCount));
                        badgeFull.setVisibility(View.VISIBLE);
                    } else {
                        badgeFull.setVisibility(View.GONE);
                    }
                }

                TextView badgeHazardous = activity.findViewById(R.id.nav_badge_hazardous);
                if (badgeHazardous != null) {
                    if (hazardousCount > 0) {
                        badgeHazardous.setText(String.valueOf(hazardousCount));
                        badgeHazardous.setVisibility(View.VISIBLE);
                    } else {
                        badgeHazardous.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupNavProfileRow() {
        View profileRow = activity.findViewById(R.id.nav_profile_row);
        if (profileRow != null) {
            profileRow.setOnClickListener(v ->
                    BaseActivity.redirectActivity(activity, ProfileActivity.class));
        }
    }
}