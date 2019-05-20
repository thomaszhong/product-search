package com.thomas.productsearch;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
Similar product tab fragment
*/
public class SimilarProductsTab extends Fragment implements AdapterView.OnItemSelectedListener{

    private View view;
    private List<SimilarItem> productList;
    private RecyclerView recyclerView;
    private SimilarItemAdapter adapter;
    private List<SimilarItem> originalProductList;
    private int sortCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_similar_products_tab, container, false);
        productList = new ArrayList<>();
        sortCounter = 0;
        Spinner sortBySpinner = view.findViewById(R.id.sortBySpinner);
        Spinner sortOrderSpinner = view.findViewById(R.id.sortOrderSpinner);
        sortBySpinner.setOnItemSelectedListener(this);
        sortOrderSpinner.setOnItemSelectedListener(this);
        sortOrderSpinner.setEnabled(false);
        return view;
    }

    public void setAdapter(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject item = response.getJSONObject(i);
                productList.add(new SimilarItem(
                    item.getString("photo"),
                    item.getString("title"),
                    item.getString("shipping"),
                    item.getString("daysLeft"),
                    item.getString("price"),
                    item.getString("itemURL")
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        originalProductList = new ArrayList<>(productList);
        recyclerView = view.findViewById(R.id.recycleViewSimilarItems);
        adapter = new SimilarItemAdapter(getActivity(), productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SimilarItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(productList.get(position).getStoreURL()));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (sortCounter++ > 1) {
            Spinner sortBySpinner = this.view.findViewById(R.id.sortBySpinner);
            Spinner sortOrderSpinner = this.view.findViewById(R.id.sortOrderSpinner);
            String sortBy = sortBySpinner.getSelectedItem().toString();
            String sortOrder = sortOrderSpinner.getSelectedItem().toString();
            if (sortBy.equals("Default")) {
                sortOrderSpinner.setEnabled(false);
                productList = new ArrayList<>(originalProductList);
                adapter = new SimilarItemAdapter(getActivity(), productList);
                recyclerView.setAdapter(adapter);
            } else {
                sortOrderSpinner.setEnabled(true);
                Collections.sort(productList, new ProductComparator(sortBy, sortOrder));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
