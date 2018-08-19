package com.psquare.delivery; /**
 * Created by DELL on 11-04-2018.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.psquare.delivery.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

/**
 * Created by Admin on 04-11-2017.
 */
public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private String latitude, longitude;
    private TimerTask timerTask;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private JSONObject jsonObject;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private CallBack callBack;
    private AppPreference preference;
    private String apiUrl;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        preference = new AppPreference(getApplicationContext());
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        apiUrl = null;
        if (preference.getUserType().equals("1")) {
            apiUrl = Url.UPLOAD_LOCATION;
        } else {
            apiUrl = Url.UPLOAD_LOCATION_SURVEY;

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: response " + response);
                                try {
                                    if (response.getInt("success") == 2) {
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
                                        stopSelf();

                                        Toast.makeText(getApplicationContext(), "After 8 PM you must need to logout", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        ApplicationManager.getInstance().addToRequestQueue(jsonObjectRequest);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 10000);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, DeliveryMainActivity.class);
            notificationIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Intent notificationIntent1 = new Intent(this, SurveyCurrentLocationActivity.class);
            notificationIntent1.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            notificationIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent;
            if (preference.getUserType().equals("1")) {
                pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent1, 0);
            }


//            Notification notification = new NotificationCompat.Builder(this)
//                    .setContentTitle("Psquare Delivery App")
//                    .setTicker("Psquare Delivery App")
//                    .setContentText("Tap to open app")
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentIntent(pendingIntent)
//                    .setOngoing(true)
//                    .build();
//            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();

        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            timerTask.cancel();
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
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
        Log.d(TAG, "onConnected: location " + location);
        if (location != null) {
            Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
        if (callBack != null) {
            callBack.callBack();
        } else {
            Log.d(TAG, "getLocation: call is null");
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: location change " + location);
        Log.d(TAG, "onLocationChanged: Time " + new Date());
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        try {
            jsonObject = new JSONObject();
            if (preference.getUserType().equals("1")) {
                jsonObject.put("Delivery_id", "1");
            } else {
                jsonObject.put("pincodeId", preference.getPincode());
            }
            jsonObject.put("userId", preference.getUserId());   //    private AppPreference
            jsonObject.put("latitude", String.valueOf(location.getLatitude()));
            jsonObject.put("longitude", location.getLongitude());

            Log.d(TAG, "onLocationChanged: json ojbect " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
