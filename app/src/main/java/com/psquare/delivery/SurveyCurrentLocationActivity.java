package com.psquare.delivery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SurveyCurrentLocationActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION = 1;
    private ArrayList<LatLng> markerPointsArrayList;
    private ArrayList<Double> latArrayList = new ArrayList<>();
    private ArrayList<Double> longiArrayList = new ArrayList<>();
    private AppPreference preference;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 1;
    private LocationRequest locationRequest;
    private static final String TAG = SurveyCurrentLocationActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private Marker marker;
    private LatLng oldLocation, newLocation;
    private float oldBearing = 0;
    private Intent locationServiceIntent;
    private Marker userMarker;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ActionBar actionbar;
    boolean firstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_current_location);
        mDrawerLayout = findViewById(R.id.drawer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.Show_area) {
                            // Toast.makeText(SurveyCurrentLocationActivity.this, "Survey Click", Toast.LENGTH_SHORT).show();
                            Intent mintent = new Intent(SurveyCurrentLocationActivity.this, SurveyCoveredMapActivity.class);
                            mintent.putExtra("FROM_FLAG", "1");
                            startActivity(mintent);
                        } else if (menuItem.getItemId() == R.id.area) {
                            // Toast.makeText(SurveyCurrentLocationActivity.this, "Survey Click", Toast.LENGTH_SHORT).show();
                            Intent aintent = new Intent(SurveyCurrentLocationActivity.this, SurveyCoveredMapActivity.class);
                            aintent.putExtra("FROM_FLAG", "2");
                            startActivity(aintent);
                        } else if (menuItem.getItemId() == R.id.change_password) {
                            Toast.makeText(SurveyCurrentLocationActivity.this, "Changing password", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SurveyCurrentLocationActivity.this, ChangePasswordActivity.class);
                            startActivity(intent);

                        } else if (menuItem.getItemId() == R.id.logout1) {

                            logout();


                        } else if (menuItem.getItemId() == R.id.add_survey) {
                            Intent intent = new Intent(SurveyCurrentLocationActivity.this, SurveyMainActivity.class);
                            startActivity(intent);

                        } else if (menuItem.getItemId() == R.id.show_km) {
                            Intent intent = new Intent(SurveyCurrentLocationActivity.this, DistanceActivity.class);
                            startActivity(intent);
                        } else if (menuItem.getItemId() == R.id.pincode_area) {
                            Intent pintent = new Intent(SurveyCurrentLocationActivity.this, SurveyMapActivity.class);
                            pintent.putExtra("FROM_FLAG", "1");

                            startActivity(pintent);
                        }

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        preference = new AppPreference(this);
        latArrayList = ApplicationManager.getInstance().getArrayLat();
        longiArrayList = ApplicationManager.getInstance().getArrayLongi();
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
            AppPermission.requestLocationPermission(SurveyCurrentLocationActivity.this, LOCATION_PERMISSION);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void startLocationService() {
        // locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        // startService(locationServiceIntent);
        locationServiceIntent = new Intent(SurveyCurrentLocationActivity.this, LocationService.class);
        locationServiceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(locationServiceIntent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
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
            Log.d(TAG, "onConnected: ");
            getLocation();
        } else {
            AppPermission.requestLocationPermission(this, 1);
        }
    }

    private void getLocation() {
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        //if (location != null) {
        // Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        //  startService();
        // }
    }

    //private void startService() {
    //    Intent serviceIntent = new Intent(this, LocationService.class);
    //   startService(serviceIntent);
    //}

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        oldLocation = new LatLng(Double.parseDouble("18.5585072"), Double.parseDouble("73.7750742"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(oldLocation);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter));
        markerOptions.flat(true);
        markerOptions.title("Your Current Location");
        marker = googleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(oldLocation)
                .zoom(17f)
                .build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition
                        (cameraPosition));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Please grant location permission", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(newLocation);
        marker.setAnchor(0.5f, 0.5f);
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
        marker.setRotation(bearing);

        oldLocation = newLocation;
        oldBearing = marker.getRotation();

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

    public void logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SurveyCurrentLocationActivity.this);
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
        Log.d(TAG, "SessionLogout: json " + jsonObject);
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

                        Toast.makeText(SurveyCurrentLocationActivity.this, "Successfully Logged Out", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SurveyCurrentLocationActivity.this, LoginActivity.class);
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
}
