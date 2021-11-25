package com.example.swapapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
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

        al = new ArrayList<SwipeCard>();
        al.add(new SwipeCard("very cool logo\nwhoever made this is so cool\nworth at least $2000", "atlas_logo_transparent"));
        al.add(new SwipeCard("background thingy", "ic_launcher_background"));
        al.add(new SwipeCard("foreground thingy", "ic_launcher_foreground"));
        al.add(new SwipeCard("background thingy yet again", "ic_launcher_background"));
        al.add(new SwipeCard("$2001 since its green", "atlas_logo_green"));

        arrayAdapter = new SwipeCardAdapter(this, getLayoutInflater(), al);

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        TextView text1 = findViewById(R.id.text1);
        text1.setText(al.get(0).getText1());

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
                updateCard();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(SwipeItemsActivity.this, "Right!", Toast.LENGTH_SHORT).show();
                updateCard();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add(new SwipeCard("XML ".concat(String.valueOf(i)), "gradient"));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
            private void updateCard() {
                text1.setText(al.get(0).getText1());
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
