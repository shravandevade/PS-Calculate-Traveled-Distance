package com.psquare.delivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.psquare.delivery.distance.dagger.HomeViewModule;
import com.psquare.delivery.distance.model.entity.ActivityType;
import com.psquare.delivery.distance.presenter.HomePresenter;
import com.psquare.delivery.distance.view.HomeView;
import com.psquare.delivery.distance.view.activity.MeasurementActivity;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeliveryMainActivity extends AppCompatActivity implements HomeView, LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION = 1;
    private GoogleMap map;
    private ArrayList<LatLng> markerPointsArrayList;
    private ArrayList<Double> latArrayList = new ArrayList<>();
    private ArrayList<Double> longiArrayList = new ArrayList<>();
    private ArrayList<Delivery_details> addressArrayList = new ArrayList<>();
    private Button btn_view_path;
    private ProgressDialog progress;
    private DrawerLayout mDrawerLayout;
    private Intent locationServiceIntent;
    private ActionBarDrawerToggle mDrawerToggle;
    private LatLng oldLocation;
    private Marker userMarker;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private LatLng newLocation;
    private float oldBearing;
    private AppPreference preference;
    boolean firstTime = true;
    private String str_origin;




    @Inject
    HomePresenter presenter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Bind(R.id.fab_startstop)
    FloatingActionButton floatingActionButtonStartStop;
    @Bind(R.id.linearlayout_warn)
    LinearLayout linearLayoutWarn;
    @Bind(R.id.textview_warn)
    TextView textViewWarn;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main);

        preference = new AppPreference(this);
        latArrayList = ApplicationManager.getInstance().getArrayLat();
        longiArrayList = ApplicationManager.getInstance().getArrayLongi();
        addressArrayList = ApplicationManager.getInstance().getArrayAddress();
        markerPointsArrayList = new ArrayList<LatLng>();

        mDrawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        btn_view_path = findViewById(R.id.btn_view_path);
        btn_view_path.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DeliveryMainActivity.this, RecyclerListFragment.class);
                startActivity(i);
            }
        });

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (AppPermission.checkLocationPermission(this) == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        } else {
            AppPermission.requestLocationPermission(DeliveryMainActivity.this, LOCATION_PERMISSION);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.add_survey) {
                            addSurvey();
                        } else if (menuItem.getItemId() == R.id.logout1) {
                            logout();
                        } else if (menuItem.getItemId() == R.id.change_password) {
                            Intent intent = new Intent(DeliveryMainActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);

                        } else if (menuItem.getItemId() == R.id.show_km) {
                            Intent intent = new Intent(DeliveryMainActivity.this, MeasurementActivity.class);
                            startActivity(intent);
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);

        Button btnDraw = (Button) findViewById(R.id.btn_draw);
        btnDraw.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (latArrayList.size() > 0) {
                    progress = new ProgressDialog(DeliveryMainActivity.this);
                    progress.setMessage("Downloading data");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.show();
                    String url = getDirectionsUrl();
                    DownloadTask downloadTask = new DownloadTask(false);
                    downloadTask.execute(url);
                } else {
                    Toast.makeText(DeliveryMainActivity.this, "No delivery Available", Toast.LENGTH_SHORT).show();
                }
            }
        });






        ButterKnife.bind(this);
        ((ApplicationManager) getApplication()).getObjectGraph().plus(new HomeViewModule(this)).inject(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Verifying ACCESS_FINE_LOCATION permission. If negative, requesting the permission.
        int accessFineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessFineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        floatingActionButtonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callTrackingService();
            }
        });
        presenter.init();
    }

    private void startLocationService() {
        //locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        // startService(locationServiceIntent);
        locationServiceIntent = new Intent(DeliveryMainActivity.this, LocationService.class);
        locationServiceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(locationServiceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        } else {
            Toast.makeText(this, "Please grant location permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeliveryMainActivity.this);
        alertDialog.setTitle("Logout"); // Sets title for your alertbox
        alertDialog.setMessage("Are you sure you want to Logout ?"); // Message to be displayed on alertbox
        alertDialog.setIcon(R.drawable.logout); // Icon for your alertbox
        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                SessionLogout();
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void SessionLogout() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionId", preference.getSessionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "SessionLogout: json " + jsonObject);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url.GET_LOGOUT_TIME, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", "onResponse: " + response);
                try {
                    if (response.getInt("success") == 1) {
                        String username = null, password = null;
                        boolean isSaveLogin = false;
                        if (preference.isSaveLogin()) {
                            username = preference.getUsername();
                            password = preference.getPassword();
                            isSaveLogin = preference.isSaveLogin();
                        }
                        preference.clearPreference();
                        preference.setUsername(username);
                        preference.setPassword(password);
                        preference.setSaveLogin(isSaveLogin);
                        stopService(locationServiceIntent);

                        Toast.makeText(DeliveryMainActivity.this, "Successfully Logged Out", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DeliveryMainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public void addSurvey() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeliveryMainActivity.this);
        alertDialog.setTitle("Survey"); // Sets title for your alertbox
        alertDialog.setMessage("Are you sure you want to Add Survey ?"); // Message to be displayed on alertbox
        // alertDialog.setIcon(R.drawable.logout); // Icon for your alertbox
        /* When positive (yes/ok) is clicked */
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeliveryMainActivity.this, SurveyMainActivity.class);
                startActivity(intent);
            }
        });

        /* When negative (No/cancel) button is clicked*/
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private String getDirectionsUrl() {
        if (newLocation == null)
            str_origin = "origin=" + 18.5802755 + "," + 73.8178089;
        else
            str_origin = "origin=" + newLocation.latitude + "," + newLocation.longitude;

        String str_dest = "destination=" + latArrayList.get(latArrayList.size() - 1) + "," + longiArrayList.get(longiArrayList.size() - 1);
        String sensor = "sensor=false";
        String waypoints = "";
        waypoints = "waypoints=";
        for (int i = 0; i < latArrayList.size() - 1; i++) {
            waypoints += latArrayList.get(i) + "," + longiArrayList.get(i) + "|";
        }
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d("TAG", "getDirectionsUrl: " + url);
        return url;
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

    @SuppressLint("MissingPermission")
    private void getLocation() {
        createLocationRequest();
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        oldLocation = new LatLng(Double.parseDouble("18.5802755"), Double.parseDouble("73.8178089"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(oldLocation);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter));
        markerOptions.flat(true);
        markerOptions.title("Your Current Location");
        userMarker = googleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(oldLocation)
                .zoom(17f)
                .build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition
                        (cameraPosition));

        if (latArrayList == null || latArrayList.size() == 0) {
            Log.d("TAG", "onMapReady: lat long null");
            return;
        }
        for (int i = 0; i < latArrayList.size(); i++) {
            if (i == latArrayList.size() - 1) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latArrayList.get(i), longiArrayList.get(i)))
                        .title("End Location ")
                        .snippet(addressArrayList.get(i).getContact())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.endmarker)));
            } else if (i == 0) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latArrayList.get(i), longiArrayList.get(i)))
                        .title("Start Location")
                        .snippet(addressArrayList.get(i).getContact())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.startmarker)));
            } else {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latArrayList.get(i), longiArrayList.get(i)))
                        .title(" " + i)
                        .snippet(addressArrayList.get(i).getContact())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.waypointmarker)));
            }
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (AppPermission.checkLocationPermission(this) == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "onConnected: ");
            getLocation();
        } else {
            AppPermission.requestLocationPermission(this, 1);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        markerPointsArrayList.add(newLocation);
        userMarker.setPosition(newLocation);
        userMarker.setAnchor(0.5f, 0.5f);
        float bearing = getBearing(oldLocation, newLocation);

        if (firstTime) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newLocation)
                    .zoom(17f)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition
                            (cameraPosition));
            firstTime = false;
        }
        if (String.valueOf(bearing).equals("NaN")) {
            bearing = oldBearing;
        }
        Log.d("TAG", "onLocationChanged: bearing " + bearing);
        userMarker.setRotation(bearing);

        oldLocation = newLocation;
        oldBearing = userMarker.getRotation();

       /* PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(markerPointsArrayList);
        lineOptions.width(7);
        lineOptions.color(Color.GREEN);
        map.addPolyline(lineOptions);*/
        int forLoopIterator = markerPointsArrayList.size() / 21;
        /*if (forLoopIterator == 0) {
            forLoopIterator = 1;
        }*/
        int remainingPoints = markerPointsArrayList.size() % 21;
        Log.d("TAG", "onLocationChanged: marker size " + markerPointsArrayList.size());
        String url = null;
        if (forLoopIterator != 0) {
            for (int i = 0; i < forLoopIterator; i++) {
                int m = i * 21;
                int k = ((i + 1)) * 21;
                String str_origin = "origin=" + markerPointsArrayList.get(m).latitude + "," + markerPointsArrayList.get(m).longitude;
                String str_dest = "destination=" + markerPointsArrayList.get(k - 1).latitude + "," + markerPointsArrayList.get(k - 1).longitude;
                String sensor = "sensor=false";
                String waypoints = "";
                waypoints = "waypoints=";
                for (int j = i * 21; j < k; j++) {
                    waypoints += markerPointsArrayList.get(j).latitude + "," + markerPointsArrayList.get(j).longitude;
                    if (j % 21 == 20) {

                    } else {
                        waypoints = waypoints + "|";
                    }
                }
                String parameters = str_origin + "&" + str_dest + "&" + sensor + "&alternatives=false" + "&" + waypoints;
                String output = "json";
                url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDONNEyR007gOPO0u-3s6qYx9De_NCVrwQ";
                Log.d("TAG", "getDirectionsUrl: " + url);
                DownloadTask downloadTask = new DownloadTask(true);
                downloadTask.execute(url);
            }
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        private boolean fromCurrentRoute;

        public DownloadTask(boolean fromCurrentRoute) {
            this.fromCurrentRoute = fromCurrentRoute;
        }

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
            ParserTask parserTask = new ParserTask(fromCurrentRoute);
            parserTask.execute(result);
            if (progress != null)
                progress.dismiss();
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        private boolean fromCurrentRoute;

        public ParserTask(boolean fromCurrentRoute) {
            this.fromCurrentRoute = fromCurrentRoute;
        }

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
                if (fromCurrentRoute)
                    lineOptions.color(Color.GREEN);
                else {
                    lineOptions.color(Color.BLUE);
                }
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }






    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.measurement:
                startActivity(MeasurementActivity.newInstance(this));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void warnWasNotPossibleToCaptureLocation(String errorMessage) {
        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(getResources().getColor(R.color.indigo500Alpha));
        toast.show();
    }

    @Override
    public void show(com.psquare.delivery.distance.model.entity.Location location, ActivityType activityType) {
        linearLayoutWarn.setVisibility(View.GONE);
        googleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions.position(latLng);
        markerOptions.title(activityType.getName());
        BitmapDescriptor bitmapDescriptor;
        switch (activityType) {
            case ON_BICYCLE:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_bicycle);
                break;
            case UNKNOWN:
            case DEFAULT:
            default:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_unknown);
                break;
        }
        markerOptions.icon(bitmapDescriptor);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void warnWasNotPossibleToRecognizeActivity(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void warnTracking() {
        textViewWarn.setText(getString(R.string.activityhome_warntracking));
        linearLayoutWarn.setVisibility(View.VISIBLE);
        floatingActionButtonStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
    }

    @Override
    public void warnTrackingHasBeenStopped() {
        linearLayoutWarn.setVisibility(View.GONE);
        floatingActionButtonStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0f));
        Toast.makeText(this, R.string.activityhome_warntrackingpaused, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showStopButton() {
        floatingActionButtonStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
    }

    @Override
    public void showPlayButton() {
        floatingActionButtonStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
    }
}
