package com.psquare.delivery;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

public class DistanceActivity extends AppCompatActivity {

    TextView Total, Daily;
    double lat1, lat2;
    private AppPreference preference;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        Total = (TextView) findViewById(R.id.total);
        Daily = (TextView) findViewById(R.id.daily);

        preference = new AppPreference(this);
        getGPSAddressForDistance();
        getGPSAddressForDailyDistance();
    }

    private void getGPSAddressForDistance() {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", preference.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url;
        if (preference.getUserType().equals("1")) {
            url = Url.GET_Delivery_distance;
        } else {
            url = Url.GET_distance;
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DistanceResponsePojo distanceResponsePojo = gson.fromJson(response.toString(), DistanceResponsePojo.class);
                double totalDistance = 0;
                String date = distanceResponsePojo.getAddressess()[0].getDate();
                for (int i = 0; i < distanceResponsePojo.getAddressess().length - 1; i++) {
//                    if (!distanceResponsePojo.getAddressess()[i].getLatitude().isEmpty() && !distanceResponsePojo.getAddressess()[i + 1].getLatitude().isEmpty()) {
                    if (date.equals(distanceResponsePojo.getAddressess()[i+1].getDate())) {
                        if (distanceResponsePojo.getAddressess()[i].getLatitude() != null && distanceResponsePojo.getAddressess()[i + 1].getLatitude() != null) {
                            lat1 = Double.parseDouble(distanceResponsePojo.getAddressess()[i].getLatitude());
                            double long1 = Double.parseDouble(distanceResponsePojo.getAddressess()[i].getLongitude());
                            lat2 = Double.parseDouble(distanceResponsePojo.getAddressess()[i + 1].getLatitude());
                            double long2 = Double.parseDouble(distanceResponsePojo.getAddressess()[i + 1].getLongitude());

                            Location startLocation = new Location("Start Location");
                            startLocation.setLatitude(lat1);
                            startLocation.setLongitude(long1);
                            Location endLocation = new Location("End Location");
                            endLocation.setLatitude(lat2);
                            endLocation.setLongitude(long2);
                            totalDistance = totalDistance + (startLocation.distanceTo(endLocation));
                        }
                    }
                    date = distanceResponsePojo.getAddressess()[i+1].getDate();
//    totalDistance = totalDistance+distance(lat1,long1,lat2,long2);
                }
                Log.d("TAG", "onResponse: total distance " + totalDistance);
                double totalDistanceInKM = totalDistance / 1000;
                Total.setText("Total Distance (KM):- " + String.format("%.2f", totalDistanceInKM));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getGPSAddressForDailyDistance() {
        JSONObject jsonObject1 = null;
        jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("user_id", preference.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url1;
        if (preference.getUserType().equals("1")) {
            url1 = Url.GET_Daily_Deliverydistance;
        } else {
            url1 = Url.GET_Daily_Surveydistance;
        }


        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, url1, jsonObject1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DistanceResponsePojo distanceResponsePojo = gson.fromJson(response.toString(), DistanceResponsePojo.class);
                double totalDistance = 0;

                for (int i = 0; i < distanceResponsePojo.getAddressess().length - 1; i++) {
//                    if (!distanceResponsePojo.getAddressess()[i].getLatitude().isEmpty() && !distanceResponsePojo.getAddressess()[i + 1].getLatitude().isEmpty()) {
                    if (distanceResponsePojo.getAddressess()[i].getLatitude() != null && distanceResponsePojo.getAddressess()[i + 1].getLatitude() != null) {
                        lat1 = Double.parseDouble(distanceResponsePojo.getAddressess()[i].getLatitude());
                        double long1 = Double.parseDouble(distanceResponsePojo.getAddressess()[i].getLongitude());
                        lat2 = Double.parseDouble(distanceResponsePojo.getAddressess()[i + 1].getLatitude());
                        double long2 = Double.parseDouble(distanceResponsePojo.getAddressess()[i + 1].getLongitude());

                        GetDistanceFromLatLonInKm(lat1, long1, lat2, long2);

                        Location startLocation = new Location("Start Location");
                        startLocation.setLatitude(lat1);
                        startLocation.setLongitude(long1);
                        Location endLocation = new Location("End Location");
                        endLocation.setLatitude(lat2);
                        endLocation.setLongitude(long2);
                        totalDistance = totalDistance + (startLocation.distanceTo(endLocation));
                    }
//    totalDistance = totalDistance+distance(lat1,long1,lat2,long2);
                }
                Log.d("TAG", "onResponse: total distance " + totalDistance);
                double totalDistanceInKM = totalDistance / 1000;
//                Daily.setText("Daily Distance (KM):- " + String.format("%.2f", totalDistanceInKM));

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest1);
    }

    public double GetDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2)
    {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km

        Daily.setText("Daily Distance (KM):- " + String.format("%.2f", d));
        return d;
    }
    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }
}

