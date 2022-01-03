package com.example.swapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import java.util.ArrayList;
import java.util.Iterator;

public class SwipeItemsActivity extends Activity {
    private FirebaseDatabase db;
    private ArrayList<SwipeCard> mCards;
    private SwipeCardAdapter arrayAdapter;

    private String itemid;
    private String uid;
    private ArrayList<String> trades;
    private int userItemCount;

    private TextView name;
    private TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_items);

        Intent intent = getIntent();
        itemid = intent.getStringExtra("item_id");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db = FirebaseDatabase.getInstance();
        mCards = new ArrayList<SwipeCard>();
        arrayAdapter = new SwipeCardAdapter(this, getLayoutInflater(), mCards);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);

        trades = new ArrayList<String>();
        DatabaseReference tradeList = db.getReference("trades").child(itemid);
        tradeList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot trade : snapshot.getChildren()) {
                        trades.add(trade.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference userCount = db.getReference("users").child(uid).child("item_count");
        userCount.get().addOnCompleteListener(task -> {
            userItemCount = task.getResult().getValue(Integer.class);
        });

        DatabaseReference childCount = db.getReference().child("item_count");
        childCount.get().addOnCompleteListener(task -> {
            int totalItems = task.getResult().getValue(Integer.class);
            if (totalItems - userItemCount - trades.size() <= 0) {
                Log.d(null, "there are no items left");
            } else {
                Log.d(null, "there are items left");
            }
        });

        mCards.add(new SwipeCard("abcdefg"));
        updateCard();

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                mCards.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                String objectId = ((SwipeCard) dataObject).getId();
                DatabaseReference newTrade = db.getReference("trades").child(itemid).child(objectId);
                newTrade.setValue(false);
                trades.add(objectId);
                updateCard();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                String objectId = ((SwipeCard) dataObject).getId();
                DatabaseReference newTrade = db.getReference("trades").child(itemid).child(objectId);
                newTrade.setValue(true);
                trades.add(objectId);

                DatabaseReference checkTrade = db.getReference("trades").child(objectId);
                checkTrade.get().addOnCompleteListener(task -> {
                    if (task.getResult().hasChild(itemid) && task.getResult().child(itemid).getValue(Boolean.class)) {
                        // trade goes here
                    }
                });

                updateCard();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                getNewCardsTest(mCards);
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }

        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(SwipeItemsActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCard() {
        DatabaseReference cardRef = db.getReference("items").child(mCards.get(0).getId());
        cardRef.get().addOnCompleteListener(task -> {
            name.setText(task.getResult().child("name").getValue(String.class));
            desc.setText(task.getResult().child("description").getValue(String.class));
        });
    }

    public void getNewCardsTest(ArrayList<SwipeCard> cards) {
        DatabaseReference childCount = db.getReference().child("item_count");
        childCount.get().addOnCompleteListener(task -> {
            int totalItems = task.getResult().getValue(Integer.class);
            if (totalItems - userItemCount - trades.size() > 0) {
                DatabaseReference itemsList = db.getReference("items");
                itemsList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean searching = true;
                        while (searching) {
                            int rand = (int) Math.floor(Math.random() * 5);
                            Iterator iterator = snapshot.getChildren().iterator();
                            for (int i = 0; i < rand; i++) {
                                iterator.next();
                            }

                            DataSnapshot selected = (DataSnapshot) iterator.next();
                            String id = selected.getKey();
                            if (!selected.child("uid").getValue(String.class).equals(uid) && !trades.contains(id)) {
                                SwipeCard card = new SwipeCard(id);
                                cards.add(card);
                                searching = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {

            }
        });
    }
}
