package com.thomas.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
Wish List tab fragment
*/
public class WishListTab extends Fragment {

    private View view;
    List<Product> productList;
    RecyclerView recyclerView;
    private ProductAdapter adapter;
    private SharedPreferences sharedPreferences;
    private double totalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wish_list_tab, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recyclerViewWishList);
        sharedPreferences = getActivity().getSharedPreferences("wishList", Context.MODE_PRIVATE);
        setAdapter();
        return view;
    }

    public void setAdapter() {
        view.findViewById(R.id.wishListNoResultsMessage).setVisibility(View.VISIBLE);
        view.findViewById(R.id.recyclerViewWishList).setVisibility(View.GONE);
        productList = new ArrayList<>();
        Gson gson = new Gson();
        Map<String, ?> keys = sharedPreferences.getAll();
        totalPrice = 0.00;
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String json = entry.getValue().toString();
            Product product = gson.fromJson(json, Product.class);
            productList.add(product);
            totalPrice += Double.parseDouble(product.getPrice());
        }
        TextView wishListCounter = view.findViewById(R.id.wishListCounter);
        TextView wishListTotal = view.findViewById(R.id.wishListTotal);
        wishListCounter.setText(Html.fromHtml("<font color='#FFFFFF'>Wishlist total("
                + productList.size() + " items):</font>"));
        NumberFormat formatter = new DecimalFormat("#0.00");
        wishListTotal.setText(Html.fromHtml("<b><font color='#FFFFFF'>$"
                + formatter.format(totalPrice) + "</font></b>"));
        if (!keys.isEmpty()) {
            view.findViewById(R.id.wishListNoResultsMessage).setVisibility(View.GONE);
            view.findViewById(R.id.recyclerViewWishList).setVisibility(View.VISIBLE);
        } else {
            return;
        }
        adapter = new ProductAdapter(getActivity(), productList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(getActivity(), ItemDetails.class);

                Gson gson = new Gson();
                String json = gson.toJson(product);
                intent.putExtra("product", json);
                startActivity(intent);
            }

            @Override
            public void onCartClick(int position) {
                Product product = productList.get(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(product.getItemId());
                editor.apply();
                productList.remove(position);
                TextView wishListCounter = view.findViewById(R.id.wishListCounter);
                TextView wishListTotal = view.findViewById(R.id.wishListTotal);
                wishListCounter.setText(Html.fromHtml("<font color='#FFFFFF'>Wishlist total("
                        + productList.size() + " items):</font>"));
                totalPrice -= Double.parseDouble(product.getPrice());
                if (totalPrice < 0) {
                    totalPrice = 0;
                }
                NumberFormat formatter = new DecimalFormat("#0.00");
                wishListTotal.setText(Html.fromHtml("<b><font color='#FFFFFF'>$"
                        + formatter.format(totalPrice) + "</font></b>"));
                Toast.makeText(getActivity(), product.getShortTitle()
                        + " was removed from wish list", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                if (sharedPreferences.getAll().isEmpty()) {
                    view.findViewById(R.id.wishListNoResultsMessage).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.recyclerViewWishList).setVisibility(View.GONE);
                }
            }
        });
    }
}
