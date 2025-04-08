package com.example.moviecatalog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;
    private TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        signupLink = findViewById(R.id.signup_link);

        loginButton.setOnClickListener(v -> loginUser());
        signupLink.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void loginUser() {
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (emailStr.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (passwordStr.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
