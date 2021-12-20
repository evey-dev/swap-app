package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends Activity {
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.button_view_profile).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyProfileActivity.class)));

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

        findViewById(R.id.button_list_new).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ListNewActivity.class)));

        ArrayList<String> ids = new ArrayList<String>();
        db.getReference("items").orderByChild("uid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ids.clear();
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

        itemList.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, view, i, l) -> {
            Intent intent = new Intent(HomeActivity.this, ListNewActivity.class);
            intent.putExtra("item_id", ids.get(i));
            startActivity(intent);
        });

    }
}
