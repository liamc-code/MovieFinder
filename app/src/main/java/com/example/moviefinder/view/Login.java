package com.example.moviefinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviefinder.R;
import com.example.moviefinder.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Register click event
        binding.registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentObj = new Intent(getApplicationContext(), Register.class);
                startActivity(intentObj);
            }
        });

        // Login click event
        Animation clickAnim = AnimationUtils.loadAnimation(this,R.anim.button_click);
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(clickAnim);
                email = binding.emailEditText.getText().toString().trim();
                password = binding.passwordEditText.getText().toString().trim();

                if(email.isEmpty()) {
                    binding.emailEditText.setError("Email is required");
                    Toast.makeText(Login.this, "Email/Password is Incorrect.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.isEmpty()) {
                    binding.passwordEditText.setError("Password is required");
                    Toast.makeText(Login.this, "Email/Password is Incorrect.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                signIn(email, password);
            }
        });

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intentObj = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentObj);
                } else {
                    Log.d("tag", "signInWithEmail:failure",
                            task.getException());
                    Toast.makeText(Login.this, "Email/Password is Incorrect.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}