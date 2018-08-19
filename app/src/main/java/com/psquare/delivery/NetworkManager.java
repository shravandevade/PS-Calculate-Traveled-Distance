package com.psquare.delivery;

/**
 * Created by DELL on 11-04-2018.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 04-11-2017.
 */

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();
    private static NetworkManager mInstance;

    public static NetworkManager getInstance() {
        return (mInstance != null) ? mInstance : new NetworkManager();
    }

    public void sendJsonObjectRequest(JSONObject jsonObject, String apiUrl) {
        Log.e(TAG, "sendJsonObjectRequest: ");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

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
