package com.psquare.delivery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, CallBack, OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 2;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 1;
    private LocationRequest locationRequest;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private Marker marker;
    private LatLng oldLocation, newLocation;
    private float oldBearing = 0;
    private Gson gson = new Gson();
    private ArrayList<Double> latArrayList, longArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latArrayList = new ArrayList<>();
        longArrayList = new ArrayList<>();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
            Log.d(TAG, "onConnected: ");
            getLocation();
        } else {
            AppPermission.requestLocationPermission(this, 1);
        }

    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
    }

    private void getLocation() {
       /* PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MY LOCK");
        wakeLock.acquire();*/
        createLocationRequest();
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        if (location != null) {
            Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            startService();
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, Location.class);
        startService(serviceIntent);
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
        /*Log.d("TAG", "onLocationChanged: location change " + location);
        Log.d("TAG", "onLocationChanged: Time " + new Date());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", "1");
            jsonObject.put("lat", String.valueOf(location.getLatitude()));
            jsonObject.put("long", location.getLongitude());
            NetworkManager.getInstance().sendJsonObjectRequest(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
//        googleMap.clear();

//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
        newLocation = new LatLng(location.getLatitude(), location.getLongitude());


        marker.setPosition(newLocation);
        marker.setAnchor(0.5f, 0.5f);
        float bearing = getBearing(oldLocation, newLocation);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(newLocation)
                .zoom(17f)
//                .bearing(90)
                //   .tilt(45)
                .build();
        if (String.valueOf(bearing).equals("NaN")) {
            bearing = oldBearing;
        }
        Log.d(TAG, "onLocationChanged: bearing " + bearing);
        marker.setRotation(bearing);
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition
                        (cameraPosition));
        oldLocation = newLocation;
        oldBearing = marker.getRotation();
    }

    @Override
    public void callBack() {
        Log.d(TAG, "callBack: ");
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        oldLocation = new LatLng(Double.parseDouble("18.5585072"), Double.parseDouble("73.7750742"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(oldLocation);
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
        markerOptions.flat(true);
        markerOptions.title("Your Current Location");
        marker = googleMap.addMarker(markerOptions);
    }
}
