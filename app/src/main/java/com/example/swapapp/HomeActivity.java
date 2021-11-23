package com.example.swapapp;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends Activity {
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("users").child(user.getUid());
        dbRef.child("username").get().addOnCompleteListener(task ->
                ((TextView) findViewById(R.id.user_welcome)).setText("Welcome " + task.getResult().getValue() + "!")
        );

        ListView itemList = findViewById(R.id.item_list);
        itemList.setEmptyView(findViewById(R.id.empty_list_text));

        String[] ids = {"abcdefg", "abcdefg1","abcdefg1","abcdefg1","abcdefg1","abcdefg1","abcdefg1"};
        ItemListAdapter adapter = new ItemListAdapter(this, ids);
        itemList.setAdapter(adapter);


    }
}
