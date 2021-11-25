package com.example.swapapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;

public class ItemListAdapter extends BaseAdapter {
    Context context;
    String[] ids;
    private static LayoutInflater inflater = null;

    public ItemListAdapter(Context context, String[] ids) {
        this.context = context;
        this.ids = ids;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object getItem(int position) {
        return ids[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
            v = inflater.inflate(R.layout.list_item, null);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("items").child(ids[position]);
        TextView text = v.findViewById(R.id.item_name);
        dbRef.child("name").get().addOnCompleteListener(task ->
                text.setText(String.valueOf(task.getResult().getValue())));

        CircleImageView image = v.findViewById(R.id.item_image);

        dbRef.child("image").get().addOnCompleteListener(task -> {
            StorageReference photoReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(task.getResult().getValue()));
            try {
                final File localFile = File.createTempFile("temp","jpg");
                photoReference.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            image.setImageBitmap(bitmap);
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return v;
    }
}