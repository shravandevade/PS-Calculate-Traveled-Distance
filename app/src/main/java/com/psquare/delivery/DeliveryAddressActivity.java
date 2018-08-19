package com.psquare.delivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;


public class DeliveryAddressActivity extends Activity {

    private AppPreference preferences;
    private ArrayList<Double> latArrayList;
    private ArrayList<Double> longiArrayList;
    private ArrayList<Delivery_details> addressArrayList;
    private ArrayList<Integer> deliveryIdArrayList;
    private Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        latArrayList = new ArrayList<>();
        longiArrayList = new ArrayList<>();
        addressArrayList = new ArrayList<>();
        deliveryIdArrayList = new ArrayList<>();
        preferences = new AppPreference(this);
        getDeliveryDetails();
    }

    private void getDeliveryDetails() {
        MainRequestModel requestModel = new MainRequestModel(preferences.getUserId());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(requestModel));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getDeliveryDetails: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.DELIVERY_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", "onResponse: ");
                latArrayList.clear();
                longiArrayList.clear();
                MainResponseModel mainResponseModel = gson.fromJson(response.toString(), MainResponseModel.class);
                for (int i = 0; i < mainResponseModel.getDelivery_details().length; i++) {
                    latArrayList.add(Double.parseDouble(mainResponseModel.getDelivery_details()[i].getLatitude()));
                    longiArrayList.add(Double.parseDouble(mainResponseModel.getDelivery_details()[i].getLongitude()));
                    addressArrayList.add(mainResponseModel.getDelivery_details()[i]);
                }
                ApplicationManager.getInstance().setArrayLat(latArrayList);
                ApplicationManager.getInstance().setArrayLongi(longiArrayList);
                ApplicationManager.getInstance().setArrayAddress(addressArrayList);
                Log.d("TAG", "onResponse: lat size " + latArrayList.size());
                Log.d("TAG", "onResponse: llong size " + longiArrayList.size());
                Log.d("TAG", "onResponse: address size " + addressArrayList.size());
                Intent i = new Intent(DeliveryAddressActivity.this, DeliveryMainActivity.class);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(
                            response.data,
                            "UTF-8"
                    );
                    Log.d("TAG", "parseNetworkResponse: json " + json);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}

