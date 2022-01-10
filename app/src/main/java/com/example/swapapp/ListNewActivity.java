package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ListNewActivity extends Activity {
    ImageView item_image;
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    StorageReference photoReference;
    DatabaseReference dbRef;
    boolean Default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_new);

        Button button_back = findViewById(R.id.button_back);
        Button button_save = findViewById(R.id.button_save);
        Button button_upload_image = findViewById(R.id.button_upload_image);
        TextInputLayout description = findViewById(R.id.input_description);
        TextInputLayout name = findViewById(R.id.input_name);
        String item_id;
        String uid;

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            item_id = intent.getStringExtra("item_id");
        }
        else {
            item_id = "" + System.currentTimeMillis();
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db.getReference("items").child(item_id).child("description").setValue("No description set");
            db.getReference("items").child(item_id).child("image").setValue("item_pictures/default.png");
            db.getReference("items").child(item_id).child("name").setValue("No name set");
            db.getReference("items").child(item_id).child("uid").setValue(uid);

            db.getReference("items").child(item_id).child("view_count").setValue(0);
            db.getReference("item_count").get().addOnCompleteListener(task -> {
                db.getReference("item_count").setValue(task.getResult().getValue(Integer.class) + 1);
            });
            db.getReference("users").child(uid).child("item_count").get().addOnCompleteListener(task -> {
               db.getReference("users").child(uid).child("item_count").setValue(task.getResult().getValue(Integer.class) + 1);
            });

        }

        item_image = (ImageView) findViewById(R.id.image_item);

        dbRef = db.getReference("items").child(item_id);

        dbRef.get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            photoReference = mStorageReference.child(String.valueOf(snapshot.child("image").getValue()));
            GlideApp.with(this).load(photoReference).signature(new ObjectKey(System.currentTimeMillis())).into(item_image);

            description.getEditText().setText(snapshot.child("description").getValue().toString());
            name.getEditText().setText(snapshot.child("name").getValue().toString());

        });
        button_back.setOnClickListener(view -> finish());

        button_upload_image.setOnClickListener(view -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 3);
        });

        description.addOnEditTextAttachedListener(textInputLayout -> {
            description.setErrorEnabled(false);
        });

        Toast toast = Toast.makeText(this, "Your item has been saved", Toast.LENGTH_SHORT);

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

            String newName = name.getEditText().getText().toString();
            if(name.getEditText().getText().length() < 1) {
                name.setError("Please input an item name");
                return;
            }
            if(name.getEditText().getText().length() > 20) {
                name.setError("Your item name cannot be longer than 20 characters");
                return;
            }
            name.setErrorEnabled(false);
            dbRef.child("name").setValue(newName);

            DatabaseReference tradeRef = db.getReference().child("trades").child(item_id).child("1641247844499");
            tradeRef.setValue(false);

            toast.show();
            Intent swipeIntent = new Intent(ListNewActivity.this, SwipeItemsActivity.class);
            swipeIntent.putExtra("item_id", item_id);
            startActivity(swipeIntent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            dbRef.child("image").get().addOnCompleteListener(task -> {
                if(task.getResult().getValue().toString().equals("item_pictures/default.png")) {
                    Default = true;
                } else {
                    Default = false;
                }
                photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));

                Uri selectedImage = data.getData();
                ImageView image = (ImageView) findViewById(R.id.image_item);
                String imageName = "item_pictures/"+selectedImage.getLastPathSegment();

                StorageReference newImage = mStorageReference.child(imageName);
                UploadTask uploadTask = newImage.putFile(selectedImage);

                uploadTask.addOnFailureListener(e -> {
                    Toast.makeText(this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    image.setImageURI(selectedImage);
                    Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                    dbRef.child("image").setValue(imageName);
                });

                if(!Default) {
                    photoReference.delete().addOnSuccessListener(runnable -> {
                    });
                }
            });
        }
    }
}