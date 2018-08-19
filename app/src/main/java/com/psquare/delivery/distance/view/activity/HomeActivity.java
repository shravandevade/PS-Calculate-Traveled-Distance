package com.psquare.delivery.distance.view.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.psquare.delivery.R;
import com.psquare.delivery.distance.GetDistanceApplication;
import com.psquare.delivery.distance.dagger.HomeViewModule;
import com.psquare.delivery.distance.model.entity.ActivityType;
import com.psquare.delivery.distance.model.entity.Location;
import com.psquare.delivery.distance.presenter.HomePresenter;
import com.psquare.delivery.distance.view.HomeView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeView, OnMapReadyCallback {

    @Inject
    HomePresenter presenter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Bind(R.id.fab_startstop)
    FloatingActionButton floatingActionButtonStartStop;
    @Bind(R.id.linearlayout_warn)
    LinearLayout linearLayoutWarn;
    @Bind(R.id.textview_warn)
    TextView textViewWarn;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        ((GetDistanceApplication) getApplication()).getObjectGraph().plus(new HomeViewModule(this)).inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);

        //Verifying ACCESS_FINE_LOCATION permission. If negative, requesting the permission.
        int accessFineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessFineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        floatingActionButtonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callTrackingService();
            }
        });

        presenter.init();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
    public void show(Location location, ActivityType activityType) {
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
            case STILL:
            case TILTING:
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_still);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

}

