package com.example.stfgames;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

public class ImageAdapter extends ArrayAdapter<Uri> {
    public ImageAdapter(Context context, ArrayList<Uri> uris) {
        super(context, R.layout.image_item, R.id.imageItem, uris);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView imageView = view.findViewById(R.id.imageItem);
        Uri uri = getItem(position);

        // Aquí podrías usar una librería como Picasso o Glide para cargar la imagen
        Glide.with(getContext()).load(uri).into(imageView);

        return view;
    }
}
