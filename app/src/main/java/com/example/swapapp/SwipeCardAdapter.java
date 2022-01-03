package com.example.swapapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SwipeCardAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList mSwipeCardArrayList;
    FirebaseDatabase db;
    StorageReference photoReference;

    public SwipeCardAdapter(Context context, LayoutInflater layoutInflater, ArrayList<SwipeCard> swipeCardArraylist) {
        this.mContext = context;
        this.mLayoutInflater = layoutInflater;
        this.mSwipeCardArrayList = swipeCardArraylist;
        db = FirebaseDatabase.getInstance();
    }

    @Override
    public int getCount() {
        return mSwipeCardArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSwipeCardArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.swipe_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (TextView) convertView.findViewById(R.id.card_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String id = ((SwipeCard) this.getItem(position)).getId();
        DatabaseReference item = db.getReference("items").child(id);
        viewHolder.image.setText("test");

        return convertView;
    }

    private static class ViewHolder {
        public TextView image;
    }
}