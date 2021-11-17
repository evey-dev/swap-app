package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            if (error) return;
        });
        findViewById(R.id.button_forgot_password).setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });
//        findViewById(R.id.button_sign_up).setOnClickListener(view -> {
//            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
//        });
    }
}
