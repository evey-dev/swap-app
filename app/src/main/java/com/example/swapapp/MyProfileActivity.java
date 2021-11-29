package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.w3c.dom.Text;
import java.io.File;
import java.io.IOException;

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
        dbRef.child("profile_image").get().addOnCompleteListener(task -> {
            StorageReference photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));
            try {
                final File localFile = File.createTempFile("temp","jpg");
                photoReference.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            pfp.setImageBitmap(bitmap);
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        for (UserInfo profile : user.getProviderData()) {
            ((TextView) findViewById(R.id.text_display_username)).setText(profile.getDisplayName());
            ((TextView) findViewById(R.id.text_display_email)).setText(profile.getEmail());
        }


        dbRef.child("reputation").get().addOnCompleteListener(task ->
                ((TextView) findViewById(R.id.text_display_reputation)).setText(task.getResult().getValue().toString())
        );

        TextInputLayout description = findViewById(R.id.input_description);
        dbRef.child("description").get().addOnCompleteListener(task ->
                ((TextInputLayout) findViewById(R.id.input_description)).getEditText().setText(task.getResult().getValue().toString())
        );

        button_save.setOnClickListener(view -> {
            String newDescription = description.getEditText().getText().toString();
            //update description on firebase

            //show update confirmation
        });
    }
}
