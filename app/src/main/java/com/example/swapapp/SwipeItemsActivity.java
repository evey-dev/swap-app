package com.example.swapapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class SwipeItemsActivity extends Activity {
    private ArrayList<SwipeCard> al;
    private SwipeCardAdapter arrayAdapter;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_items);


//        al = new ArrayList<>();
//        al.add("php");
//        al.add("c");
//        al.add("python");
//        al.add("java");
//        al.add("html");
//        al.add("c++");
//        al.add("css");
//        al.add("javascript");
//
//        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al );
        al = new ArrayList<SwipeCard>();
        al.add(new SwipeCard("card1text1", "atlas_logo_transparent"));
        al.add(new SwipeCard("card2text1", "ic_launcher_background"));
        al.add(new SwipeCard("card3text1", "ic_launcher_foreground"));
        al.add(new SwipeCard("card4text1", "ic_launcher_background"));
        al.add(new SwipeCard("card5text1", "atlas_logo_transparent"));

        arrayAdapter = new SwipeCardAdapter(this, getLayoutInflater(), al);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(SwipeItemsActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(SwipeItemsActivity.this, "Right!", Toast.LENGTH_SHORT).show();;
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add(new SwipeCard("XML ".concat(String.valueOf(i)), "test"));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
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

}
