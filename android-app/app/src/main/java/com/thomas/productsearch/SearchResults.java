package com.thomas.productsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResults extends AppCompatActivity {

    private List<Product> productList;
    private ProductAdapter adapter;
    private RequestQueue requestQueue;
    private int resumeCounter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        requestQueue = Volley.newRequestQueue(this);
        productList = new ArrayList<>();

        resumeCounter = 0;
        sharedPreferences = getSharedPreferences("wishList", MODE_PRIVATE);
        searchProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumeCounter++ > 0) {
            for (Product product : productList) {
                if (sharedPreferences.contains(product.getItemId())) {
                    product.setCartId(R.drawable.cart_remove);
                } else {
                    product.setCartId(R.drawable.cart_plus);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchProducts() {
        Bundle extras = getIntent().getExtras();

        final String keywordInput = extras.getString("keyword");
        String categorySpinner = extras.getString("category");
        boolean newCheck = extras.getBoolean("new");
        boolean usedCheck = extras.getBoolean("used");
        boolean unspecifiedCheck = extras.getBoolean("unspecified");
        boolean localPickupCheck = extras.getBoolean("local");
        boolean freeShippingCheck = extras.getBoolean("free");
        boolean nearbySearchCheck = extras.getBoolean("nearbySearch");
        String distanceInput = extras.getString("distance");
        String zipCodeInput = extras.getString("zipCode");

        if (distanceInput.isEmpty()) {
            distanceInput = "10";
        }

        String url = "http://productsearchandroid-env.upi67yignh.us-west-1.elasticbeanstalk.com/searchProducts?";
        try {
            url += "keyword=" + URLEncoder.encode(keywordInput, "UTF-8") + "&category=" + categorySpinner
                    + "&new=" + newCheck + "&used=" + usedCheck + "&unspecified=" + unspecifiedCheck
                    + "&local=" + localPickupCheck + "&free=" + freeShippingCheck + "&nearbySearch="
                    + nearbySearchCheck + "&distance=" + distanceInput + "&zipCode=" + zipCodeInput;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("searchProductsURL", url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {

                    JSONObject item = null;
                    int cartId = R.drawable.cart_plus;
                    try {
                        item = response.getJSONObject(i);
                        if (sharedPreferences.contains(item.getString("itemId"))) {
                            cartId = R.drawable.cart_remove;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Create ProductDetails object to store data
                    ProductDetails productDetails = null;
                    try {
                        productDetails = new ProductDetails(
                                item.getString("feedbackScore"),
                                item.getDouble("popularity")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        productDetails.setStoreName(item.getString("storeName"));
                    } catch (JSONException e) {
                        productDetails.setStoreName("");
                    }
                    try {
                        productDetails.setStoreURL(item.getString("buyProductAt"));
                    } catch (JSONException e) {
                        productDetails.setStoreName("");
                    }
                    try {
                        productDetails.setHandlingTime(item.getString("handlingTime"));
                    } catch (JSONException e) {
                        productDetails.setHandlingTime("");
                    }
                    //Add Product object to the list
                    try {
                        productList.add(new Product(
                                item.getString("photo"),
                                item.getString("title"),
                                item.getString("titleShort"),
                                item.getString("zipCode"),
                                item.getString("shipping"),
                                item.getString("condition"),
                                item.getString("price"),
                                cartId,
                                item.getString("itemId"),
                                productDetails
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TextView searchResultsMessage =  findViewById(R.id.searchResultsMessage);
                String coloredNumOfItems = "<font color='#FC572F'>" + productList.size() + "</font>";
                String coloredKeyword = "<font color='#FC572F'>" + keywordInput + "</font>";
                searchResultsMessage.setText(Html.fromHtml("<b>Showing " + coloredNumOfItems
                        + " results for " + coloredKeyword + "</b>"));
                if (!productList.isEmpty()) {
                    findViewById(R.id.searchNoResultMessage).setVisibility(View.GONE);
                    searchResultsMessage.setVisibility(View.VISIBLE);
                    findViewById(R.id.recyclerViewItem).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.searchNoResultMessage).setVisibility(View.VISIBLE);
                }
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.progressBarMessage).setVisibility(View.GONE);
                setAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public void setAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItem);
        adapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Product product = productList.get(position);
                Intent intent = new Intent(SearchResults.this, ItemDetails.class);

                Gson gson = new Gson();
                String json = gson.toJson(product);
                intent.putExtra("product", json);

                startActivity(intent);
            }

            @Override
            public void onCartClick(int position) {
                Product product = productList.get(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (sharedPreferences.contains(product.getItemId())) {
                    product.setCartId(R.drawable.cart_plus);
                    editor.remove(product.getItemId());
                    editor.apply();
                    Toast.makeText(SearchResults.this, product.getShortTitle()
                            + " was removed from wish list", Toast.LENGTH_SHORT).show();
                } else {
                    product.setCartId(R.drawable.cart_remove);
                    Gson gson = new Gson();
                    String json = gson.toJson(product);
                    editor.putString(product.getItemId(), json);
                    editor.apply();
                    Toast.makeText(SearchResults.this, product.getShortTitle()
                            + " was added to wish list", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
