package com.psquare.delivery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.psquare.delivery.utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SurveyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Gson gson;
    String fromFlag;
    private AppPreference preference;
    private ProgressDialog progress;
    private ArrayList<String> pincodes;
    private PincodeResponse pincodeResponse;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_map);
        gson = new Gson();
        preference = new AppPreference(this);
        fromFlag = getIntent().getStringExtra("FROM_FLAG");
        Log.i("fromFlag22",""+fromFlag);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        spinner = findViewById(R.id.pincode_spinner);
        if (fromFlag.equals("1")) {
            pincodes = new ArrayList<>();
            getAssignPincode();
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (pincodeResponse != null) {
                        String selectedPincodeId = pincodeResponse.getData()[i].getId();
                        if (selectedPincodeId != null) {
                            mMap.clear();
                            getSurveyLatLng(selectedPincodeId);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            spinner.setVisibility(View.GONE);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in CurrentLoc and move the camera
        LatLng CurrentLoc = new LatLng(Double.parseDouble("18.5802755"), Double.parseDouble("73.8178089"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CurrentLoc));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CurrentLoc)
                .zoom(17f)
                .build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition
                        (cameraPosition));
        if (fromFlag.equals("2")) {
            getSurveyLatLng(null);
        }
    }

    private void getAssignPincode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", preference.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.ASSING_PINCODE, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", "onResponse: assigned pincode " + response);
                pincodeResponse = gson.fromJson(response.toString(), PincodeResponse.class);
                if (pincodeResponse.getSuccess() == 1) {
                    for (int i = 0; i < pincodeResponse.getData().length; i++) {
                        String postPincode = pincodeResponse.getData()[i].getPost() + " - " + pincodeResponse.getData()[i].getPincode();
                        pincodes.add(postPincode);

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SurveyMapActivity.this, android.R.layout.simple_spinner_dropdown_item, pincodes);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void getSurveyLatLng(String pincodeId) {
        progress = new ProgressDialog(SurveyMapActivity.this);
        progress.setMessage("Downloading data");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        int requestMethod;
        String url;
        JSONObject jsonObject = null;
        if (fromFlag.equals("1")) {
//By me
            requestMethod = Request.Method.POST;
            url = Url.MY_SURVEY_Pincode_URL;
            jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", preference.getUserId());
                jsonObject.put("pincodeId", pincodeId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
// BY All
            requestMethod = Request.Method.GET;
            url = Url.GET_SURVEY_LATLNG;

        }
        Log.d("TAG", "getSurveyLatLng: url " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<LatLng> points = new ArrayList<>();
                PolylineOptions lineOptions = new PolylineOptions();
                SurveyLocationResponseModel surveyLocationResponseModel = gson.fromJson(response.toString(), SurveyLocationResponseModel.class);
                int forLoopIterator = surveyLocationResponseModel.getLocations().length / 21;
                if (forLoopIterator == 0) {
                    forLoopIterator = 1;
                }
                int remainingPoints = surveyLocationResponseModel.getLocations().length % 21;

                String url = null;
                for (int i = 0; i < forLoopIterator; i++) {
                    int m = i * 21;
                    int k = ((i + 1)) * 21;
                    String str_origin = "origin=" + surveyLocationResponseModel.getLocations()[m].getLatitude() + "," + surveyLocationResponseModel.getLocations()[m].getLongitude();
                    String str_dest = "destination=" + surveyLocationResponseModel.getLocations()[k - 1].getLatitude() + "," + surveyLocationResponseModel.getLocations()[k - 1].getLongitude();
                    String sensor = "sensor=false";
                    String waypoints = "";
                    waypoints = "waypoints=";
                    for (int j = i * 21; j < k; j++) {
                        waypoints += surveyLocationResponseModel.getLocations()[j].getLatitude() + "," + surveyLocationResponseModel.getLocations()[j].getLongitude();
                        if (j % 21 == 20) {

                        } else {
                            waypoints = waypoints + "|";
                        }
                    }
                    String parameters = str_origin + "&" + str_dest + "&" + sensor + "&alternatives=false" + "&" + waypoints;
                    String output = "json";
                    url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDONNEyR007gOPO0u-3s6qYx9De_NCVrwQ";
                    Log.d("TAG", "getDirectionsUrl: " + url);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }

                /*for (int i = 0; i < surveyLocationResponseModel.getLocations().length; i++) {

                    double lat = Double.parseDouble(surveyLocationResponseModel.getLocations()[i].getLatitude());
                    double lng = Double.parseDouble(surveyLocationResponseModel.getLocations()[i].getLongitude());
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);

                }
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.RED);
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);*/
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        })

        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        ApplicationManager.getInstance().

                addToRequestQueue(jsonObjectRequest);
    }


    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("TAG", "onPostExecute: " + result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.RED);
                progress.dismiss();
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

}
