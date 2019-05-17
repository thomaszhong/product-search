package com.thomas.productsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class ItemDetails extends AppCompatActivity {
    private ProductDetailsTab productDetailsTab;
    private ShippingTab shippingTab;
    private GooglePhotosTab googlePhotosTab;
    private SimilarProductsTab similarProductsTab;

    private RequestQueue requestQueue;

    private Gson gson;
    private Product product;
    private int price;
    private String itemURL;

    private SharedPreferences sharedPreferences;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        productDetailsTab = new ProductDetailsTab();
        shippingTab = new ShippingTab();
        googlePhotosTab = new GooglePhotosTab();
        similarProductsTab = new SimilarProductsTab();

        Toolbar toolbar = findViewById(R.id.toolbar2);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.container2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = findViewById(R.id.tabs2);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(ItemDetails.this, R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(ItemDetails.this, R.color.colorAccent);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestQueue = Volley.newRequestQueue(this);
        Bundle extras = getIntent().getExtras();

        gson = new Gson();
        String json = extras.getString("product");
        product = gson.fromJson(json, Product.class);

        setTitle(product.getShortTitle());

        getItemDetails();
        getGoogleSearchPhotos();
        getSimilarItems();

        fab = findViewById(R.id.floatingActionButton);
        sharedPreferences = getSharedPreferences("wishList", MODE_PRIVATE);
        if (sharedPreferences.contains(product.getItemId())) {
            fab.setImageResource(R.drawable.cart_remove_white);
        } else {
            fab.setImageResource(R.drawable.cart_plus_white);
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

    public void facebookShare(View view) {
        String url = "https://www.facebook.com/dialog/share?app_id=1411280842347974&display=page&href="
                + itemURL + "&hashtag=%23CSCI571Spring2019Ebay&quote=Buy " + URLEncoder.encode(product.getTitle())
                + " for $" + price + " from Ebay!";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void toggleWishList(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains(product.getItemId())) {
            fab.setImageResource(R.drawable.cart_plus_white);
            editor.remove(product.getItemId());
            editor.apply();
            Toast.makeText(ItemDetails.this, product.getShortTitle()
                    + " was removed from wish list", Toast.LENGTH_SHORT).show();
        } else {
            fab.setImageResource(R.drawable.cart_remove_white);
            product.setCartId(R.drawable.cart_remove);
            editor.putString(product.getItemId(), gson.toJson(product));
            editor.apply();
            Toast.makeText(ItemDetails.this, product.getShortTitle()
                    + " was added to wish list", Toast.LENGTH_SHORT).show();
        }
    }

    public void getItemDetails() {
        String url = "http://productsearchandroid-env.upi67yignh.us-west-1.elasticbeanstalk.com/getItemDetails?itemId=" + product.getItemId();
        Log.i("getItemDetailsURL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    price = response.getInt("price");
                    itemURL = response.getString("itemURL");
                    JSONArray photos = response.getJSONArray("photos");
                    productDetailsTab.setGalleryView(photos);
                    productDetailsTab.setTextViews(product, response);
                    shippingTab.setTextViews(product, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                findViewById(R.id.progressBar2).setVisibility(View.GONE);
                findViewById(R.id.progressBarMessage2).setVisibility(View.GONE);
                findViewById(R.id.productDetailsDisplay).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar3).setVisibility(View.GONE);
                findViewById(R.id.progressBarMessage3).setVisibility(View.GONE);
                findViewById(R.id.shippingDisplay).setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public void getGoogleSearchPhotos() {
        String url = "http://productsearchandroid-env.upi67yignh.us-west-1.elasticbeanstalk.com/getGoogleSearchPhotos?title=" + URLEncoder.encode(product.getTitle());
        Log.i("getGoogleSearchPhotosURL", url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                googlePhotosTab.setAdapter(response);
                findViewById(R.id.progressBar4).setVisibility(View.GONE);
                findViewById(R.id.progressBarMessage4).setVisibility(View.GONE);
                findViewById(R.id.recyclerViewGooglePhoto).setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public void getSimilarItems() {
        String url = "http://productsearchandroid-env.upi67yignh.us-west-1.elasticbeanstalk.com/getSimilarItems?itemId=" + product.getItemId();
        Log.i("getSimilarItemsURL", url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    similarProductsTab.setAdapter(response);
                } else {
                    findViewById(R.id.similarProductsNoResultMessage).setVisibility(View.VISIBLE);
                    findViewById(R.id.recycleViewSimilarItems).setVisibility(View.GONE);
                    findViewById(R.id.sortBySpinner).setEnabled(false);
                    findViewById(R.id.sortOrderSpinner).setEnabled(false);
                }
                findViewById(R.id.similarProductsDisplay).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar5).setVisibility(View.GONE);
                findViewById(R.id.progressBarMessage5).setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return productDetailsTab;
                case 1:
                    return shippingTab;
                case 2:
                    return googlePhotosTab;
                case 3:
                    return similarProductsTab;
            }
            return null;
    }
        @Override
        public int getCount() {
            return 4;
        }
    }
}
