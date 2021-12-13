package com.example.swapapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.w3c.dom.Text;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MyProfileActivity extends Activity {
    ImageView pfp;
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference("users").child(user.getUid());
    StorageReference photoReference;
    boolean Default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Button button_back = findViewById(R.id.button_back);
        Button button_save = findViewById(R.id.button_save);
        Button button_upload_pfp = findViewById(R.id.button_upload_pfp);
        pfp = (ImageView) findViewById(R.id.image_user_pfp);

        dbRef.child("profile_image").get().addOnCompleteListener(task -> {
            photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));
            GlideApp.with(this).load(photoReference).signature(new ObjectKey(System.currentTimeMillis())).into(pfp);
        });

        button_back.setOnClickListener(view -> finish());

        button_upload_pfp.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
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
        description.addOnEditTextAttachedListener(textInputLayout -> {
            description.setErrorEnabled(false);
        });
        Toast toast = Toast.makeText(this, "Your new description has been saved", Toast.LENGTH_SHORT);
        button_save.setOnClickListener(view -> {
            String newDescription = description.getEditText().getText().toString();
            if(description.getEditText().getText().length() < 1) {
                description.setError("Please input a description");
                return;
            }
            if(description.getEditText().getText().length() > 100) {
                description.setError("Your description cannot be longer than 100 characters");
                return;
            }
            description.setErrorEnabled(false);
            dbRef.child("description").setValue(newDescription);

            toast.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            dbRef.child("profile_image").get().addOnCompleteListener(task -> {
                if(task.getResult().getValue().toString().equals("user_profiles/default.jpg")) {
                    Default = true;
                } else {
                    Default = false;
                }
                photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));

                Uri selectedImage = data.getData();
                ImageView pfp = (ImageView) findViewById(R.id.image_user_pfp);
                String imageName = "user_profiles/"+selectedImage.getLastPathSegment();

                StorageReference newpfp = mStorageReference.child(imageName);
                UploadTask uploadTask = newpfp.putFile(selectedImage);

                uploadTask.addOnFailureListener(e -> {
                    Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    pfp.setImageURI(selectedImage);
                    Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                    dbRef.child("profile_image").setValue(imageName);
                });

                if(!Default) {
                    photoReference.delete().addOnSuccessListener(runnable -> {
                    });
                }
            });
        }
    }
}