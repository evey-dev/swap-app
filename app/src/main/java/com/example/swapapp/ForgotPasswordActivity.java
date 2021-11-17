package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        TextInputLayout email_input = findViewById(R.id.email_input);
        Button button_back = findViewById(R.id.button_back);
        Button button_confirm = findViewById(R.id.button_confirm);

        button_back.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_input.getEditText().getText().toString();
                if(email.equals(""))
                    email_input.setError("Enter your email");
                else if(!email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"))
                    email_input.setError("This is an invalid email, please enter a valid email");
                else
                    email_input.setErrorEnabled(false);
                //add ooga booga codes or something
            }
        });
    }
}
