package com.example.ecosmart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, dustbins, fullDustbins, hazardousDustbins, complaints, notifications, logout;
    ImageView ivProfilePhoto, toolbarProfilePhoto;
    TextView tvProfileName, tvProfileUsername;
    EditText etName, etEmail, etUsernameReadonly;
    Button btnSave;
    LinearLayout btnLogout;
    DatabaseReference userRef;
    SessionManager sessionManager;
    String currentUsername;
    Uri selectedImageUri = null;
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK
                        && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfilePhoto.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupToolbar();

        sessionManager  = new SessionManager(this);
        currentUsername = sessionManager.getUsername();

        if (currentUsername == null) {
            redirectActivity(this, LoginActivity.class);
            return;
        }
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUsername);
        drawerLayout      = findViewById(R.id.drawerLayout);
        menu              = findViewById(R.id.menu);
        home              = findViewById(R.id.home);
        dustbins          = findViewById(R.id.dustbins);
        fullDustbins      = findViewById(R.id.full_dustbins);
        hazardousDustbins = findViewById(R.id.hazardous_dustbins);
        complaints        = findViewById(R.id.complaints);
        notifications     = findViewById(R.id.notification);
        logout            = findViewById(R.id.logout);

        ivProfilePhoto      = findViewById(R.id.iv_profile_photo);
        toolbarProfilePhoto = findViewById(R.id.toolbar_profile_photo);
        tvProfileName       = findViewById(R.id.tv_profile_name);
        tvProfileUsername   = findViewById(R.id.tv_profile_username);
        etName              = findViewById(R.id.et_profile_name);
        etEmail             = findViewById(R.id.et_profile_email);
        etUsernameReadonly  = findViewById(R.id.et_profile_username_readonly);
        btnSave             = findViewById(R.id.btn_save_profile);
        btnLogout           = findViewById(R.id.btn_profile_logout);
        ivProfilePhoto.setClipToOutline(true);
        menu.setOnClickListener(v -> openDrawer(drawerLayout));
        home.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, MainActivity.class));
        dustbins.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, DustbinActivity.class));
        fullDustbins.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, FullDustbinsActivity.class));
        hazardousDustbins.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, HazardousActivity.class));
        complaints.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, ComplaintsActivity.class));
        notifications.setOnClickListener(v ->
                redirectActivity(ProfileActivity.this, NotificationActivity.class));
        logout.setOnClickListener(v -> performLogout());

        ivProfilePhoto.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> performLogout());
        loadUserProfile();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                String name      = snapshot.child("name").getValue(String.class);
                String email     = snapshot.child("email").getValue(String.class);
                String username  = snapshot.child("username").getValue(String.class);
                String photoB64  = snapshot.child("photoUrl").getValue(String.class);

                if (name != null) {
                    etName.setText(name);
                    tvProfileName.setText(name);
                }
                if (email != null)    etEmail.setText(email);
                if (username != null) {
                    etUsernameReadonly.setText(username);
                    tvProfileUsername.setText("@" + username);
                }
                if (photoB64 != null && !photoB64.isEmpty()) {
                    loadBase64Image(photoB64, ivProfilePhoto);
                    loadBase64Image(photoB64, toolbarProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Failed to load profile",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name cannot be empty");
            etName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return;
        }
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        tvProfileName.setText(name);
        if (selectedImageUri != null) {
            convertAndSavePhoto(selectedImageUri);
        } else {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }
    private void convertAndSavePhoto(Uri imageUri) {
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imageBytes = baos.toByteArray();

            String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
            userRef.child("photoUrl").setValue(base64String)
                    .addOnSuccessListener(unused -> {

                        loadBase64Image(base64String, ivProfilePhoto);
                        loadBase64Image(base64String, toolbarProfilePhoto);

                        btnSave.setEnabled(true);
                        btnSave.setText("Save Changes");
                        selectedImageUri = null;

                        Toast.makeText(ProfileActivity.this, "Profile & photo updated!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Changes");
                        Toast.makeText(ProfileActivity.this, "Failed to save photo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (IOException e) {
            btnSave.setEnabled(true);
            btnSave.setText("Save Changes");
            Toast.makeText(this, "Could not read image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int maxSize) {
        int width  = original.getWidth();
        int height = original.getHeight();

        float scale;
        if (width > height) {
            scale = (float) maxSize / width;
        } else {
            scale = (float) maxSize / height;
        }

        int newWidth  = Math.round(width  * scale);
        int newHeight = Math.round(height * scale);
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    public static void loadBase64Image(String base64String, ImageView imageView) {
        if (base64String == null || base64String.isEmpty()) return;

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Glide.with(imageView.getContext()).load(bitmap).circleCrop().into(imageView);
            }
        } catch (Exception e) {
        }
    }

    private void performLogout() {
        sessionManager.clearSession();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}