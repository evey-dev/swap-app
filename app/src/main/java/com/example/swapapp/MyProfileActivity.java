package com.example.swapapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.w3c.dom.Text;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyProfileActivity extends Activity {
    Uri imageUri;
    ImageView pfp;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Button button_back = findViewById(R.id.button_back);
        Button button_save = findViewById(R.id.button_save);
        Button button_upload_pfp = findViewById(R.id.button_upload_pfp);
        pfp = (ImageView) findViewById(R.id.image_user_pfp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("users").child(user.getUid());

        ProgressDialog progressDialog = new ProgressDialog(this);


        button_back.setOnClickListener(v -> {
            finish();
        });

        dbRef.child("profile_image").get().addOnCompleteListener(task -> {
            StorageReference photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));
            GlideApp.with(this).load(photoReference).into(pfp);
        });

        button_upload_pfp.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 100);

            progressDialog.setTitle("Uploading File...");
            progressDialog.show();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            Date now = new Date();
            String fileName = formatter.format(now);
            mStorageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
            mStorageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    pfp.setImageURI(null);
                    Toast.makeText(MyProfileActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                }
            });
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            pfp.setImageURI(imageUri);
        }
    }
}