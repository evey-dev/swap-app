package com.example.swapapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends Activity {
    private FirebaseUser user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        dbRef.child("username").get().addOnCompleteListener(task ->
//                ((TextView) findViewById(R.id.user_welcome)).setText("Welcome " + task.getResult().getValue() + "!")
//        );
        for (UserInfo profile : user.getProviderData()) {
            ((TextView) findViewById(R.id.user_welcome)).setText("Welcome " + profile.getDisplayName() + "!");
        }

        ListView itemList = findViewById(R.id.item_list);
        itemList.setEmptyView(findViewById(R.id.empty_list_text));

        ArrayList<String> ids = new ArrayList<String>();
        db.getReference("items").orderByChild("uid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ids.add(itemSnapshot.getKey());
                }
                ItemListAdapter adapter = new ItemListAdapter(HomeActivity.this, ids);
                Log.d("home", ids.toString());
                itemList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }
}
