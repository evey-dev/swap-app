package com.example.swapapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TradeCompleteActivity extends Activity {
    private TextView itemName, otherItemName, otherItemDesc, username, email, rep;
    private Button tradeCompleted, backButton, report;
    private ImageView itemImage, otherItemImage, profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_complete);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        itemName = findViewById(R.id.item_name);
        otherItemName = findViewById(R.id.other_item_name);
        otherItemDesc = findViewById(R.id.other_item_description);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        rep = findViewById(R.id.reputation);

        tradeCompleted = findViewById(R.id.button_complete);
        backButton = findViewById(R.id.button_back);
        report = findViewById(R.id.button_report);

        itemImage = findViewById(R.id.item_image);
        otherItemImage = findViewById(R.id.other_item_image);
        profilePicture = findViewById(R.id.profile);

        Intent intent = getIntent();
        String itemid = intent.getStringExtra("itemid");
        String otherid = intent.getStringExtra("otherid");

        DatabaseReference itemref = db.getReference("items").child(itemid);
        itemref.get().addOnCompleteListener(task -> {
            String item_name = task.getResult().child("name").getValue(String.class);
            itemName.setText(item_name);

            StorageReference itemPhotoRef = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().child("image").getValue()));
            GlideApp.with(this).load(itemPhotoRef).into(itemImage);
        });

        DatabaseReference otherref = db.getReference("items").child(otherid);
        otherref.get().addOnCompleteListener(task -> {
            String other_name = task.getResult().child("name").getValue(String.class);
            otherItemName.setText(other_name);

            String other_desc = task.getResult().child("description").getValue(String.class);
            otherItemDesc.setText(other_desc);

            StorageReference otherItemPhotoRef = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().child("image").getValue()));
            GlideApp.with(this).load(otherItemPhotoRef).into(otherItemImage);

            String uid = task.getResult().child("uid").getValue(String.class);
            DatabaseReference userref = db.getReference("users").child(uid);
            userref.get().addOnCompleteListener(utask -> {
                String user_name = utask.getResult().child("username").getValue(String.class);
                username.setText(user_name);

                String user_email = utask.getResult().child("email").getValue(String.class);
                email.setText(user_email);

                int user_rep = utask.getResult().child("reputation").getValue(Integer.class);
                rep.setText(Integer.toString(user_rep));

                StorageReference userPhotoRef = FirebaseStorage.getInstance().getReference().child(String.valueOf(utask.getResult().child("profile_image").getValue()));
                GlideApp.with(this).load(userPhotoRef).into(profilePicture);
            });
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tradeCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference otherref = db.getReference("items").child(otherid);
                otherref.get().addOnCompleteListener(task -> {
                    String uid = task.getResult().child("uid").getValue(String.class);
                    DatabaseReference userref = db.getReference("users").child(uid).child("reputation");
                    userref.get().addOnCompleteListener(utask -> {
                        int currentRep = utask.getResult().getValue(Integer.class);
                        Log.d(null, Integer.toString(currentRep));
                        userref.setValue(currentRep + 1);
                    });
                });
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference otherref = db.getReference("items").child(otherid);
                otherref.get().addOnCompleteListener(task -> {
                    String uid = task.getResult().child("uid").getValue(String.class);
                    DatabaseReference userref = db.getReference("users").child(uid).child("reputation");
                    userref.get().addOnCompleteListener(utask -> {
                        int currentRep = utask.getResult().getValue(Integer.class);
                        Log.d(null, Integer.toString(currentRep));
                        userref.setValue(currentRep - 1);
                    });
                });
            }
        });
    }
}
