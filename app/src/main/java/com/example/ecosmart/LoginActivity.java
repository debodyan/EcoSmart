package com.example.ecosmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText login_username,login_password;
    Button login_button;
    TextView goto_signup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        SessionManager sessionManager = new SessionManager(LoginActivity.this);
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {


            login_username = findViewById(R.id.login_username);
            login_password = findViewById(R.id.login_password);
            login_button = findViewById(R.id.login_button);
            goto_signup = findViewById(R.id.goto_signup);

            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!validateUsername() || !validatePassword()) {
                    } else {
                        checkUser();
                    }
                }
            });

            goto_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    public boolean validateUsername(){
        String val = login_username.getText().toString();
        if(val.isEmpty()){
            login_username.setError("Username cannot be empty");
            return false;
        }else{
            login_username.setError(null);
            return true;
        }
    }
    public boolean validatePassword(){
        String val = login_password.getText().toString();
        if(val.isEmpty()){
            login_password.setError("Password cannot be empty");
            return false;
        }else{
            login_password.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String userUsername = login_username.getText().toString().trim();
        String userPassword = login_password.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    login_username.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if(passwordFromDB != null && passwordFromDB.equals(userPassword)){
                        login_password.setError(null);
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.saveUsername(userUsername);
                        Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                    login_username.setError("Invalid Credentials");
                    login_username.requestFocus();
                    }
            }else{
                Toast.makeText(LoginActivity.this,"User does not exist",Toast.LENGTH_SHORT).show();
                login_username.requestFocus();
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this,"Database Error",Toast.LENGTH_SHORT).show();
                }

        });

    }
}
