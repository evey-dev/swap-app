package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends Activity {
    private FirebaseAuth auth;
    TextInputLayout passwordInput;
    TextInputLayout usernameInput;
    TextInputLayout emailInput;
    TextInputLayout repeatInput;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        result = false;
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        repeatInput = findViewById(R.id.input_repeat_password_hint);
        passwordInput = findViewById(R.id.password_input);

        findViewById(R.id.button_confirm).setOnClickListener(view -> {
            boolean error = false;
            String username = usernameInput.getEditText().getText().toString();
            String email = emailInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();
            String repeat_password = repeatInput.getEditText().getText().toString();
            if (TextUtils.isEmpty(username)) {
                usernameInput.setError("Please enter your username");
                error = true;
            }
            else emailInput.setErrorEnabled(false);
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
            startActivity(new Intent(SignupActivity.this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.button_log_in).setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if (task.isSuccessful()) {
                result = true;
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                dbRef.child("username").setValue(usernameInput.getEditText().getText().toString());
                dbRef.child("email").setValue(email);
                dbRef.child("password").setValue(password);
            }
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

