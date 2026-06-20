package com.example.ecosmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signup_name,signup_email,signup_username,signup_password;
    TextView goto_login;
    Button signup_button;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_username = findViewById(R.id.signup_username);
        signup_password = findViewById(R.id.signup_password);
        signup_button = findViewById(R.id.signup_button);
        goto_login = findViewById(R.id.goto_login);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                String name = signup_name.getText().toString().trim();
                String email = signup_email.getText().toString().trim();
                String username = signup_username.getText().toString().trim();
                String password = signup_password.getText().toString().trim();
                if (name.isEmpty()) {
                    signup_name.setError("Name cannot be empty");
                    signup_name.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    signup_email.setError("Email cannot be empty");
                    signup_email.requestFocus();
                    return;
                }
                if (username.isEmpty()) {
                    signup_username.setError("Username cannot be empty");
                    signup_username.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    signup_password.setError("Password cannot be empty");
                    signup_password.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    signup_password.setError("Password must be at least 6 characters");
                    signup_password.requestFocus();
                    return;
                }
                database  = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                HelperClass helperClass = new HelperClass(name, email, username, password);
                reference.child(username).setValue(helperClass);

                Toast.makeText(SignupActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

    }
}