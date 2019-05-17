package com.thomas.productsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailsTab extends Fragment {

    private View view;
    private LinearLayout mGallery;
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_details_tab, container, false);
        mInflater = LayoutInflater.from(getActivity());
        return view;
    }

    public void setGalleryView(JSONArray photos) {
        mGallery = view.findViewById(R.id.photosGallery);
        for (int i = 0; i < photos.length(); i++) {
            View view = mInflater.inflate(R.layout.horizontal_scroll_view_item, mGallery, false);
            ImageView photo = view.findViewById(R.id.galleryItem);
            try {
                Picasso.with(getActivity()).load(photos.getString(i)).placeholder(R.drawable.default_image).into(photo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mGallery.addView(view);
        }
    }

    public void setTextViews(Product product, JSONObject response) {
        //Highlights table
        TextView titleText = view.findViewById(R.id.titleText);
        titleText.setText(product.getTitle());
        TextView priceText = view.findViewById(R.id.priceText);
        String shippingMessage = " With ";
        if (product.getShipping().equals("Free Shipping")) {
            shippingMessage += "Free Shipping";
        } else {
            shippingMessage += product.getShipping() + " Shipping";
        }
        priceText.setText(Html.fromHtml("<b><font color='#6200EE'>$" + product.getPrice() + "</font></b>" + shippingMessage));
        try {
            TextView subtitleText = view.findViewById(R.id.subtitleText);
            subtitleText.setText(response.getString("subtitle"));
            view.findViewById(R.id.subtitleRow).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView priceText2 = view.findViewById(R.id.priceText2);
        priceText2.setText("$" + product.getPrice());
        JSONArray itemSpecifics = null;
        try {
            itemSpecifics = response.getJSONArray("itemSpecifics");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String brandName = "";
        for (int i = 0; i < itemSpecifics.length(); i++) {
            try {
                JSONObject specific = itemSpecifics.getJSONObject(i);
                if (specific.getString("Name").equals("Brand")) {
                    brandName = specific.getJSONArray("Value").get(0).toString();
                    TextView brandText = view.findViewById(R.id.brandText);
                    brandText.setText(specific.getJSONArray("Value").get(0).toString());
                    view.findViewById(R.id.brandRow).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Specifications table
        final float scale = getContext().getResources().getDisplayMetrics().density;
        TableLayout specificationsTable = view.findViewById(R.id.specificationsTable);
        if (!brandName.isEmpty()) {
            TableRow brandRow = new TableRow(getActivity());
            TextView cellValue = new TextView(getActivity());
            cellValue.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            cellValue.setMinHeight((int) (28 * scale + 0.5f));
            cellValue.setMaxWidth((int) (350 * scale + 0.5f));
            cellValue.setText("\u2022\u00A0" + brandName);
            brandRow.addView(cellValue);
            specificationsTable.addView(brandRow);
        }
        for (int i = 0; i < itemSpecifics.length(); i++) {
            try {
                JSONObject specific = itemSpecifics.getJSONObject(i);
                if (specific.getString("Name").equals("Brand")) {
                    continue;
                }
                TableRow newRow = new TableRow(getActivity());
                TextView cellValue = new TextView(getActivity());
                cellValue.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                cellValue.setMinHeight((int) (28 * scale + 0.5f));
                cellValue.setMaxWidth((int) (350 * scale + 0.5f));
                cellValue.setText("\u2022\u00A0" + specific.getJSONArray("Value").get(0).toString());
                newRow.addView(cellValue);
                specificationsTable.addView(newRow);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
