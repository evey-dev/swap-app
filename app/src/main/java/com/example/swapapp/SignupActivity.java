package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.ktx.Firebase;

public class SignupActivity extends Activity {
    private FirebaseAuth auth;
    TextInputLayout passwordInput;
    TextInputLayout emailInput;
    TextInputLayout repeatInput;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        result = false;
        emailInput = findViewById(R.id.email_input);
        repeatInput = findViewById(R.id.input_repeat_password_hint);
        passwordInput = findViewById(R.id.password_input);

        findViewById(R.id.button_confirm).setOnClickListener(view -> {
            boolean error = false;
            String email = emailInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();
            String repeat_password = repeatInput.getEditText().getText().toString();
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Please enter your email");
                error = true;
            }
            else emailInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Please enter your password");
                error = true;
            }
            else passwordInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(repeat_password)) {
                repeatInput.setError("Please repeat your password");
                error = true;
            }
            else if (!repeat_password.equals(password)) {
                repeatInput.setError("Passwords must match");
            }
            else repeatInput.setErrorEnabled(false);
            if (error) return;
            registerUser(email, password);
            if (!result) return;
        });

        findViewById(R.id.button_log_in).setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if (task.isSuccessful())
                result = true;
            if(!task.isSuccessful()) {
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    passwordInput.setError(((FirebaseAuthWeakPasswordException) task.getException()).getReason());
                } catch(FirebaseAuthInvalidCredentialsException e) {
                    emailInput.setError("Enter a valid email");
                } catch(FirebaseAuthUserCollisionException e) {
                    emailInput.setError("An account under this email already exists");
                } catch(Exception e) {
                    Log.e("signup", e.getMessage());
                }
            }
        });
    }
}

