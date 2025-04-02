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
import com.example.moviefinder.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding binding;

    FirebaseAuth mAuth;
    String email, password, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Register event
        Animation clickAnim = AnimationUtils.loadAnimation(this,R.anim.button_click);
        binding.regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(clickAnim);
                email = binding.emailEditText.getText().toString().trim();
                password = binding.passwordEditText.getText().toString().trim();
                confirmPass = binding.confirmPassEditText.getText().toString().trim();

                boolean verifiedLogin = verifyLogin(email, password, confirmPass);

                if(verifiedLogin) {
                    registerUser(email, password);
                }
            }
        });

        // Cancel/Return to Login event
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // verify reg fields
    private boolean verifyLogin(String email, String password, String confirmPass) {
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        // clear previous errors
        binding.emailEditText.setError(null);
        binding.passwordEditText.setError(null);
        binding.confirmPassEditText.setError(null);

        // check email
        if(email.isEmpty() || !email.matches(EMAIL_REGEX)) {
            binding.emailEditText.setError("Invalid email");
            return false;
        }

        // check password (confirm with retyped pass)
        if(password.isEmpty() || !password.equals(confirmPass)) {
            binding.confirmPassEditText.setError("Incorrect Password");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password) // create user with email/pass
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // sign in successful, add user sign-in details to view
                            Log.d("tag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Successfully Registered",
                                    Toast.LENGTH_SHORT).show();

                            Intent intentObj = new Intent(getApplicationContext(), Login.class);
                            startActivity(intentObj);
                            finish();
                        } else {
                            // Sign in fails, error msg for user
                            Log.d("tag", "createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(Register.this, "Registration Failed. Try Again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}