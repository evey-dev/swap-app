package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends Activity {
    private FirebaseAuth auth;
    TextInputLayout emailInput;
    TextInputLayout passwordInput;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        result = false;
        auth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);

        findViewById(R.id.button_confirm).setOnClickListener(view -> {
            String email = emailInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();
            boolean error = false;
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Please enter your username");
                error = true;
            }
            else emailInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Please enter your password");
                error = true;
            }
            else passwordInput.setErrorEnabled(false);
            if (error) return;
            loginUser(email, password);
            if (!result) return;
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.button_forgot_password).setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });
        findViewById(R.id.button_sign_up).setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                })
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthInvalidUserException e) {
                            emailInput.setError("Email or password does not match");
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            passwordInput.setError("Email or password does not match");
                        } catch(Exception e) {
                            Log.e("login", e.getMessage());
                        }
                    }
                    else
                        result = true;
                });
    }
}
