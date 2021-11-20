package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextInputLayout emailInput = findViewById(R.id.email_input);
        TextInputLayout repeatInput = findViewById(R.id.input_repeat_password_hint);
        TextInputLayout usernameInput = findViewById(R.id.username_input);
        TextInputLayout passwordInput = findViewById(R.id.password_input);
        findViewById(R.id.button_confirm).setOnClickListener(view -> {
            boolean error = false;
            if (TextUtils.isEmpty(usernameInput.getEditText().getText().toString())) {
                usernameInput.setError("Please enter your username");
                error = true;
            }
            else usernameInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(passwordInput.getEditText().getText().toString())) {
                passwordInput.setError("Please enter your password");
                error = true;
            }
            else passwordInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(emailInput.getEditText().getText().toString())) {
                emailInput.setError("Please enter your email");
                error = true;
            }
            else if(!emailInput.getEditText().getText().toString().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"))
                emailInput.setError("This is an invalid email, please enter a valid email");
            else emailInput.setErrorEnabled(false);
            if (TextUtils.isEmpty(repeatInput.getEditText().getText().toString())) {
                repeatInput.setError("Please repeat your password");
                error = true;
            }
            else repeatInput.setErrorEnabled(false);
            if (error) return;

        });
    //    findViewById(R.id.button_log_in).setOnClickListener(view -> {
    //        LoginActivity(new Intent(LoginActivity.this));
    //        finish();
    //    });


        };
    }

