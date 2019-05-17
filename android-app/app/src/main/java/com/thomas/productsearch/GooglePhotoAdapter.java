package com.thomas.productsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GooglePhotoAdapter extends RecyclerView.Adapter<GooglePhotoAdapter.GooglePhotoViewHolder>{

    private Context context;
    private List<GooglePhoto> photoList;

    public GooglePhotoAdapter(Context context, List<GooglePhoto> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public GooglePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_google_photos, viewGroup, false);
        return new GooglePhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GooglePhotoViewHolder googlePhotoViewHolder, int i) {
        GooglePhoto photo = photoList.get(i);
        Picasso.with(context).load(photo.getImg()).placeholder(R.drawable.default_image).into(googlePhotoViewHolder.googleImage);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class GooglePhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView googleImage;
        public GooglePhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            googleImage = itemView.findViewById(R.id.googleImage);
        }
    }
}

