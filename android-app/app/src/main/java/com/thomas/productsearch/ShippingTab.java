package com.thomas.productsearch;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONException;
import org.json.JSONObject;

/**
Shipping Tab fragment
*/
public class ShippingTab extends Fragment implements View.OnClickListener{

    private View view;
    private ProductDetails productDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shipping_tab, container, false);
        return view;
    }

    public void setTextViews(Product product, JSONObject response) {
        this.productDetails = product.getProductDetails();

        //Sold By table
        if (!productDetails.getStoreName().isEmpty()) {
            TextView storeNameText = view.findViewById(R.id.storeNameText);
            storeNameText.setText(Html.fromHtml("<u>" + productDetails.getStoreName() + "</u>"));
            storeNameText.setOnClickListener(this);
            view.findViewById(R.id.storeNameRow).setVisibility(View.VISIBLE);
        }
        TextView feedbackScoreText = view.findViewById(R.id.feedbackScoreText);
        feedbackScoreText.setText(productDetails.getFeedbackScore());
        CircularScoreView popularityCircle = view.findViewById(R.id.popularityCircle);
        popularityCircle.setScore((int)Math.round(productDetails.getPopularity()));
        ImageView feedbackStarImage = view.findViewById(R.id.feedbackStarImage);
        int feedbackScore = Integer.parseInt(productDetails.getFeedbackScore());
        if (feedbackScore < 10000) {
            feedbackStarImage.setImageResource(R.drawable.star_circle_outline);
            if (feedbackScore < 10) {
                feedbackStarImage.setColorFilter(R.color.white);
            } else if (feedbackScore < 50) {
                feedbackStarImage.setColorFilter(R.color.yellow);
            } else if (feedbackScore < 100) {
                feedbackStarImage.setColorFilter(R.color.blue);
            } else if (feedbackScore < 500) {
                feedbackStarImage.setColorFilter(R.color.turquoise);
            } else if (feedbackScore < 1000) {
                feedbackStarImage.setColorFilter(R.color.purple);
            } else if (feedbackScore < 5000) {
                feedbackStarImage.setColorFilter(R.color.red);
            } else {
                feedbackStarImage.setColorFilter(R.color.green);
            }
        } else {
            feedbackStarImage.setImageResource(R.drawable.star_circle);
            if (feedbackScore < 25000) {
                feedbackStarImage.setColorFilter(R.color.yellow);
            } else if (feedbackScore < 50000) {
                feedbackStarImage.setColorFilter(R.color.turquoise);
            } else if (feedbackScore < 100000) {
                feedbackStarImage.setColorFilter(R.color.purple);
            } else if (feedbackScore < 500000) {
                feedbackStarImage.setColorFilter(R.color.red);
            } else if (feedbackScore < 1000000) {
                feedbackStarImage.setColorFilter(R.color.green);
            } else {
                feedbackStarImage.setColorFilter(R.color.silver);
            }
        }
        //Shipping Info table
        if (!product.getShipping().equals("N/A")) {
            TextView shippingCostText = view.findViewById(R.id.shippingCostText);
            shippingCostText.setText(product.getShipping());
            view.findViewById(R.id.shippingCostRow).setVisibility(View.VISIBLE);
        }
        try {
            TextView globalShippingText = view.findViewById(R.id.globalShippingText);
            boolean globalShipping = response.getBoolean("globalShipping");
            if (globalShipping) {
                globalShippingText.setText("Yes");
            } else {
                globalShippingText.setText("No");
            }
            view.findViewById(R.id.globalShippingRow).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!productDetails.getHandlingTime().isEmpty()) {
            TextView handlingTimeText = view.findViewById(R.id.handlingTimeText);
            handlingTimeText.setText(productDetails.getHandlingTime());
            view.findViewById(R.id.handlingTimeRow).setVisibility(View.VISIBLE);
        }
        if (!product.getCondition().equals("N/A")) {
            TextView conditionText = view.findViewById(R.id.conditionText);
            conditionText.setText(product.getCondition());
        }
        //Return Policy table
        try {
            TextView policyText = view.findViewById(R.id.policyText);
            policyText.setText(response.getString("returnPolicy"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            TextView returnsWithinText = view.findViewById(R.id.returnsWithinText);
            returnsWithinText.setText(response.getString("returnsWithin"));
            view.findViewById(R.id.returnsWithinRow).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            TextView refundModeText = view.findViewById(R.id.refundModeText);
            refundModeText.setText(response.getString("refundMode"));
            view.findViewById(R.id.refundModeRow).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            TextView shippedByText = view.findViewById(R.id.shippedByText);
            shippedByText.setText(response.getString("shippedBy"));
            view.findViewById(R.id.shippedByRow).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        String url = productDetails.getStoreURL();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
