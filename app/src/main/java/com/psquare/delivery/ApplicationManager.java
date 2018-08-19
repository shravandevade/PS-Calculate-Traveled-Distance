package com.psquare.delivery;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.psquare.delivery.distance.GetDistanceApplication;
import com.psquare.delivery.distance.dagger.ApiModule;
import com.psquare.delivery.distance.dagger.AppModule;
import com.psquare.delivery.distance.dagger.DaoModule;

import java.util.ArrayList;

import dagger.ObjectGraph;

public class ApplicationManager extends MultiDexApplication {
    private static final String TAG = ApplicationManager.class.getSimpleName();
    private static ApplicationManager mInstance;
    private RequestQueue requestQueue;

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mInstance = this;
        MultiDex.install(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        objectGraph = ObjectGraph.create(
                new Object[]{
                        new AppModule(ApplicationManager.this),
                        new ApiModule(),
                        new DaoModule()
                }
        );
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public static synchronized ApplicationManager getInstance() {
        Log.d(TAG, "getInstance: ");
        return (mInstance != null) ? mInstance : new ApplicationManager();
    }


    public void addToRequestQueue(Request<?> request) {
        Log.d(TAG, "addToRequestQueue: ");
        getRequestQueue().add(request);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }
    public ArrayList<Double> getArrayLat() {
        return ArrayLat;
    }

    public void setArrayLat(ArrayList<Double> arrayLat) {
        ArrayLat = arrayLat;
    }

    public ArrayList<Double> getArrayLongi() {
        return ArrayLongi;
    }

    public void setArrayLongi(ArrayList<Double> arrayLongi) {
        ArrayLongi = arrayLongi;
    }

    ArrayList<Double>ArrayLat;
    ArrayList<Double>ArrayLongi;
    ArrayList<Delivery_details>ArrayAddress;

    public ArrayList<Delivery_details> getArrayAddress() {
        return ArrayAddress;
    }

    public void setArrayAddress(ArrayList<Delivery_details> arrayAddress) {
        ArrayAddress = arrayAddress;
    }
}
