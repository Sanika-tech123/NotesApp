package com.spark.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameSignup, emailSignup, passwordSignup;
    Button buttonSignup;
    TextView goToLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameSignup = findViewById(R.id.nameSignup);
        emailSignup = findViewById(R.id.emailSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        buttonSignup = findViewById(R.id.buttonSignup);
        goToLogin = findViewById(R.id.goToLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonSignup.setOnClickListener(v -> signupUser());

        goToLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void signupUser() {
        String name = nameSignup.getText().toString().trim();
        String email = emailSignup.getText().toString().trim();
        String password = passwordSignup.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = auth.getCurrentUser().getUid();

                    // Save user info in Firestore
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);

                    db.collection("Users").document(userId)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignupActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
