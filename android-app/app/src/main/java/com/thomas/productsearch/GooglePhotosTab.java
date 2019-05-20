package com.thomas.productsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
Google Photos Tab fragment
*/
public class GooglePhotosTab extends Fragment {

    private View view;
    List<GooglePhoto> photoList;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_google_photos_tab, container, false);
        photoList = new ArrayList<>();
        return view;
    }

    public void setAdapter(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                photoList.add(new GooglePhoto(response.getString(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView = view.findViewById(R.id.recyclerViewGooglePhoto);
        GooglePhotoAdapter adapter = new GooglePhotoAdapter(getActivity(), photoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

}
