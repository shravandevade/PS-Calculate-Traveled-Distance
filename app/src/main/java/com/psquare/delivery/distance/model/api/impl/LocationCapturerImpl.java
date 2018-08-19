package com.psquare.delivery.distance.model.api.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.psquare.delivery.distance.model.api.LocationCapturer;
import com.psquare.delivery.distance.model.api.LocationCapturerListener;


public class LocationCapturerImpl implements LocationCapturer, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final long LOCATION_CAPTURING_INTERVAL_INMILIS = 10000;
    private static final long LOCATION_CAPTURING_FASTESTINTERVAL_INMILIS = 5000;
    private static final long LOCATION_CAPTURING_SMALESTDISPLACEMENT_INMETERS = 5;
    private static final float DESIRABLE_ACCURANCY = 50f;
    private GoogleApiClient googleApiClient;
    private LocationCapturerListener locationCapturerListener;

    public LocationCapturerImpl(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void startToCaptureLocations() {
        //Trying to connect with Google API. If it works onConnected() will be called, else onConnectionFailed() will be called.
        googleApiClient.connect();
    }

    @Override
    public void setLocationCapturerListener(LocationCapturerListener locationCapturerListener) {
        this.locationCapturerListener = locationCapturerListener;
    }

    @Override
    public void stopToCaptureLocations() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this); //Stopping to get location updates
            googleApiClient.disconnect(); //Closing connection with Google APIs
        }
    }

    /**
     * Called when there was an error connecting the client to the service.
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Google API connection has been failed! Stop it and warn!
        locationCapturerListener.connectionFailed(connectionResult.getErrorMessage());
        stopToCaptureLocations();
    }

    /**
     * After calling connect(), this method will be invoked asynchronously when the connect request has successfully completed.
     * After this callback, the application can make requests on other methods provided by the client and expect that no user intervention
     * is required to call methods that use account and scopes provided to the client constructor.
     *
     * @param bundle
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        //Google API connection has been done successfully, now we can
        @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_CAPTURING_INTERVAL_INMILIS);
        locationRequest.setFastestInterval(LOCATION_CAPTURING_FASTESTINTERVAL_INMILIS);
        locationRequest.setSmallestDisplacement(LOCATION_CAPTURING_SMALESTDISPLACEMENT_INMETERS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Called when the client is temporarily in a disconnected state.
     * This can happen if there is a problem with the remote service (e.g. a crash or resource problem causes it to be killed by the system).
     * When called, all requests have been canceled and no outstanding listeners will be executed.
     * GoogleApiClient will automatically attempt to restore the connection.
     * Applications should disable UI components that require the service, and wait for a call to onConnected(Bundle) to re-enable them.
     */
    @Override
    public void onConnectionSuspended(int i) {
        //Wait for the GoogleApiClient restores the connection.
    }

    /**
     * Called when the location has changed.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        //Current location has been catch, warn it!
        if(location.hasAccuracy() && location.getAccuracy() < DESIRABLE_ACCURANCY) {
            locationCapturerListener.onLocationCaptured(new com.psquare.delivery.distance.model.entity.Location(location));
        }
    }


}
