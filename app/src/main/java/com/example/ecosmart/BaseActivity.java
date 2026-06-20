package com.example.ecosmart;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    protected void setupToolbar() {

        ImageView bell = findViewById(R.id.toolbar_bell);
        if (bell != null) {
            bell.setOnClickListener(v ->
                    redirectActivity(this, NotificationActivity.class));
        }

        ImageView profilePhoto = findViewById(R.id.toolbar_profile_photo);
        if (profilePhoto != null) {
            profilePhoto.setOnClickListener(v ->
                    redirectActivity(this, ProfileActivity.class));
            loadToolbarPhoto(profilePhoto);
        }
        checkNotificationDot();
        new NavDrawerHelper(this).setupNavDrawer();
    }
    private void loadToolbarPhoto(ImageView profilePhoto) {
        SessionManager sessionManager = new SessionManager(this);
        String username = sessionManager.getUsername();
        if (username == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);
        userRef.child("photoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String photoData = snapshot.getValue(String.class);
                        if (photoData != null && !photoData.isEmpty()) {
                            ProfileActivity.loadBase64Image(photoData, profilePhoto);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void checkNotificationDot() {
        View dot = findViewById(R.id.notification_dot);
        if (dot == null) return;

        DatabaseReference dustbinRef = FirebaseDatabase.getInstance().getReference("Dustbins");

        dustbinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean hasAlert = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Dustbin dustbin = child.getValue(Dustbin.class);
                    if (dustbin == null) continue;
                    if (dustbin.getFillLevel() >= 80
                            || "Danger".equals(dustbin.getGasStatus())) {
                        hasAlert = true;
                        break;
                    }
                }
                dot.setVisibility(hasAlert ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}