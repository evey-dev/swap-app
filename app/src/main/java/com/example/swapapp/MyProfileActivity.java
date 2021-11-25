package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.w3c.dom.Text;

public class MyProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Button button_back = findViewById(R.id.button_back);
        Button button_save = findViewById(R.id.button_save);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("users").child(user.getUid());

        button_back.setOnClickListener(v -> {
            startActivity(new Intent(MyProfileActivity.this, HomeActivity.class));
            finish();
        });

        ImageView pfp = (ImageView) findViewById(R.id.image_user_pfp);
        //pfp.setImageDrawable(user.getPhotoUrl()); getPhotoUrl returns a Uri object

        dbRef.child("username").get().addOnCompleteListener(task ->
                ((TextView) findViewById(R.id.text_display_username)).setText(task.getResult().getValue().toString())
        );

        TextView email = (TextView) findViewById(R.id.text_display_email);
        email.setText(user.getEmail());

        dbRef.child("reputation").get().addOnCompleteListener(task ->
                ((TextView) findViewById(R.id.text_display_reputation)).setText(task.getResult().getValue().toString())
        );

        TextInputLayout description = findViewById(R.id.input_description);
        dbRef.child("description").get().addOnCompleteListener(task ->
                ((TextInputLayout) findViewById(R.id.input_description)).setText(task.getResult().getValue().toString())
        );

        button_save.setOnClickListener(view -> {
            String newDescription = description.getEditText().getText().toString();
            //update description on firebase

            //show update confirmation
        });
    }
}
