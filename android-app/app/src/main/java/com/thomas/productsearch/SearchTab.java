package com.thomas.productsearch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchTab extends Fragment implements View.OnClickListener {

    private View view;
    private RequestQueue requestQueue;
    private String zipCode;
    private AutoSuggestAdapter autoCompleteAdapter;
    private AppCompatAutoCompleteTextView zipCodeInput;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_search_tab, container, false);
        CheckBox nearbySearchCheck = view.findViewById(R.id.nearbySearchCheck);
        nearbySearchCheck.setOnClickListener(this);
        RadioButton currentLocationRadio = view.findViewById(R.id.currentLocationRadio);
        currentLocationRadio.setOnClickListener(this);
        RadioButton zipCodeRadio = view.findViewById(R.id.zipCodeRadio);
        zipCodeRadio.setOnClickListener(this);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        Button clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        getCurrentLocation();

        zipCodeInput = view.findViewById(R.id.zipCodeInput);
        autoCompleteAdapter = new AutoSuggestAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        zipCodeInput.setAdapter(autoCompleteAdapter);
        zipCodeInput.setThreshold(0);
        zipCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(zipCodeInput.getText())) {
                        getZipCode(zipCodeInput.getText().toString());
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void getZipCode(final String zipCode) {
        String url = "http://productsearchandroid-env.upi67yignh.us-west-1.elasticbeanstalk.com/getZipCode?zipCode=" + zipCode;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<String> zipCodeList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        zipCodeList.add(response.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                autoCompleteAdapter.setData(zipCodeList);
                autoCompleteAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    public void getCurrentLocation() {
        String url = "http://ip-api.com/json";
        zipCode = "N/A";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    zipCode = response.getString("zip");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    @Override
    public void onClick(View view) {
        EditText keywordInput = this.view.findViewById(R.id.keywordInput);
        TextView errorMessage1Text = this.view.findViewById(R.id.errorMessage1Text);
        Spinner categorySpinner = this.view.findViewById(R.id.categorySpinner);
        CheckBox newCheck = this.view.findViewById(R.id.newCheck);
        CheckBox usedCheck = this.view.findViewById(R.id.usedCheck);
        CheckBox unspecifiedCheck = this.view.findViewById(R.id.unspecifiedCheck);
        CheckBox localPickupCheck = this.view.findViewById(R.id.localPickupCheck);
        CheckBox freeShippingCheck = this.view.findViewById(R.id.freeShippingCheck);
        CheckBox nearbySearchCheck = this.view.findViewById(R.id.nearbySearchCheck);
        EditText distanceInput = this.view.findViewById(R.id.distanceInput);
        TextView textView2 = this.view.findViewById(R.id.textView2);
        RadioButton currentLocationRadio = this.view.findViewById(R.id.currentLocationRadio);
        RadioButton zipCodeRadio = this.view.findViewById(R.id.zipCodeRadio);
        AutoCompleteTextView zipCodeInput = this.view.findViewById(R.id.zipCodeInput);
        TextView errorMessage2Text = this.view.findViewById(R.id.errorMessage2Text);
        switch(view.getId()) {
            case R.id.nearbySearchCheck:
                if (nearbySearchCheck.isChecked()) {
                    distanceInput.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    currentLocationRadio.setVisibility(View.VISIBLE);
                    zipCodeRadio.setVisibility(View.VISIBLE);
                    zipCodeInput.setVisibility(View.VISIBLE);
                } else {
                    distanceInput.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    currentLocationRadio.setVisibility(View.GONE);
                    zipCodeRadio.setVisibility(View.GONE);
                    zipCodeInput.setVisibility(View.GONE);
                    errorMessage2Text.setVisibility(View.GONE);
                }
                break;
            case R.id.currentLocationRadio:
                zipCodeInput = this.view.findViewById(R.id.zipCodeInput);
                zipCodeInput.setEnabled(false);
                zipCodeInput.setText("");
                break;
            case R.id.zipCodeRadio:
                this.view.findViewById(R.id.zipCodeInput).setEnabled(true);
                break;
            case R.id.searchButton:
                boolean showErrorMessage = false;
                if (keywordInput.getText().toString().trim().isEmpty()) {
                    errorMessage1Text.setVisibility(View.VISIBLE);
                    showErrorMessage = true;
                }
                if (nearbySearchCheck.isChecked() && zipCodeInput.isEnabled() && zipCodeInput.getText().toString().trim().isEmpty()) {
                    errorMessage2Text.setVisibility(View.VISIBLE);
                    showErrorMessage = true;
                }
                if (showErrorMessage) {
                    Toast.makeText(getActivity(), "Please fix all fields with errors",
                            Toast.LENGTH_SHORT).show();
                } else {
                    int position = categorySpinner.getSelectedItemPosition();
                    String category;
                    switch (position) {
                        case 1:
                            category = "art";
                            break;
                        case 2:
                            category = "baby";
                            break;
                        case 3:
                            category = "books";
                            break;
                        case 4:
                            category = "clothing";
                            break;
                        case 5:
                            category = "default";
                            break;
                        case 6:
                            category = "computers";
                            break;
                        case 7:
                            category = "health";
                            break;
                        case 8:
                            category = "music";
                            break;
                        case 9:
                            category = "games";
                            break;
                        default:
                            category = "default";
                    }
                    Intent intent = new Intent(getActivity(), SearchResults.class);
                    intent.putExtra("keyword", keywordInput.getText().toString().trim());
                    intent.putExtra("category", category);
                    intent.putExtra("new", newCheck.isChecked());
                    intent.putExtra("used", usedCheck.isChecked());
                    intent.putExtra("unspecified", unspecifiedCheck.isChecked());
                    intent.putExtra("local", localPickupCheck.isChecked());
                    intent.putExtra("free", freeShippingCheck.isChecked());
                    intent.putExtra("nearbySearch", nearbySearchCheck.isChecked());
                    intent.putExtra("distance", distanceInput.getText().toString().trim());
                    if (currentLocationRadio.isChecked()) {
                        intent.putExtra("zipCode", zipCode);
                    } else {
                        intent.putExtra("zipCode", zipCodeInput.getText().toString().trim());
                    }
                    SearchTab.this.startActivity(intent);
                }
                break;
            case R.id.clearButton:
                keywordInput.setText("");
                errorMessage1Text.setVisibility(View.GONE);
                categorySpinner.setSelection(0);
                nearbySearchCheck.setChecked(false);
                distanceInput.setText("");
                currentLocationRadio.setChecked(true);
                zipCodeInput.setText("");
                errorMessage2Text.setVisibility(View.GONE);
                distanceInput.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                currentLocationRadio.setVisibility(View.GONE);
                zipCodeRadio.setVisibility(View.GONE);
                zipCodeInput.setVisibility(View.GONE);
                break;
        }
    }

}
