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

import androidx.activity.result.contract.ActivityResultContracts;
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

        mCards.add(new SwipeCard("1641247844499"));

        DatabaseReference childCount = db.getReference().child("item_count");
        childCount.get().addOnCompleteListener(task -> {
            int totalItems = task.getResult().getValue(Integer.class);
            if (trades.contains("1641247844499")) {
                totalItems++;
            }

            if (totalItems - 1 - userItemCount - trades.size() <= 0) {
                if(task.isSuccessful()) {
                    startActivity(new Intent(SwipeItemsActivity.this, OutOfItems.class));
                    finish();
                }
            }
        });

        name.setText("Example Item");
        desc.setText("This is an example item! \nSwipe left to reject\nSwipe right to accept.");

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
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
                Log.d(null, "adapater about to empty");
                getNewCardsTest(mCards);
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }

        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(SwipeItemsActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCard() {
        if (mCards.get(0).getId().equals("1641247844499")) {
            startActivity(new Intent(SwipeItemsActivity.this, OutOfItems.class));
            finish();
        }

        DatabaseReference cardRef = db.getReference("items").child(mCards.get(0).getId());
        cardRef.get().addOnCompleteListener(task -> {
            name.setText(task.getResult().child("name").getValue(String.class));
            desc.setText(task.getResult().child("description").getValue(String.class));
        });
    }

    public void getNewCardsTest(ArrayList<SwipeCard> cards) {
        Log.d(null, "check one");
        DatabaseReference childCount = db.getReference().child("item_count");
        childCount.get().addOnCompleteListener(task -> {
            Log.d(null, "check two");
            int totalItems = task.getResult().getValue(Integer.class);
            Log.d(null, "check three");

            if (totalItems - userItemCount - trades.size() - mCards.size() + 1 > 0) {
                Log.d(null, "items left to add");
                DatabaseReference itemsList = db.getReference("items");
                itemsList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean searching = true;
                        while (searching) {
                            int rand = (int) Math.floor(Math.random() * totalItems);
                            Iterator iterator = snapshot.getChildren().iterator();
                            for (int i = 0; i < rand; i++) {
                                iterator.next();
                            }

                            DataSnapshot selected = (DataSnapshot) iterator.next();
                            String id = selected.getKey();
                            ArrayList<String> cardStrings = new ArrayList<String>();
                            for (SwipeCard mCard : mCards) {
                                cardStrings.add(mCard.getId());
                            }
                            if (!selected.child("uid").getValue(String.class).equals(uid) && !trades.contains(id) && !cardStrings.contains(id) && !id.equals("1641247844499")) {
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
                Log.d(null, "no items left to add");
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
                cards.add(new SwipeCard("1641247844499"));
            }
        });
    }
}