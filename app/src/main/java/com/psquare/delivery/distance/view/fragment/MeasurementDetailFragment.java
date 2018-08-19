package com.psquare.delivery.distance.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.psquare.delivery.ApplicationManager;
import com.psquare.delivery.R;
import com.psquare.delivery.distance.GetDistanceApplication;
import com.psquare.delivery.distance.dagger.MeasurementDetailViewModule;
import com.psquare.delivery.distance.model.entity.ActivityLocation;
import com.psquare.delivery.distance.presenter.MeasurementDetailPresenter;
import com.psquare.delivery.distance.view.MeasurementDetailView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MeasurementDetailFragment extends Fragment implements MeasurementDetailView {

    private static final String BUNDLE_KEY_ACTIVITYLOCATION = "bundle_key_activitylocation";

    @Inject
    MeasurementDetailPresenter presenter;
    @Bind(R.id.textview_bicycle_distance)
    TextView textViewBicycleDistance;
    @Bind(R.id.textview_bicycle_time)
    TextView textViewBicycleTime;
    @Bind(R.id.textview_bicycle_averagespeed)
    TextView textViewBicycleAverageSpeed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_distance, container, false);
        ((ApplicationManager) getActivity().getApplication()).getObjectGraph()
                .plus(new MeasurementDetailViewModule(MeasurementDetailFragment.this)).inject(this);
        ButterKnife.bind(this, view);

        List<ActivityLocation> activityLocationList = getArguments().getParcelableArrayList(BUNDLE_KEY_ACTIVITYLOCATION);
        presenter.init(activityLocationList);

        return view;
    }

    public static MeasurementDetailFragment newInstance(List<ActivityLocation> activityLocationList) {
        MeasurementDetailFragment measurementDetailFragment = new MeasurementDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ACTIVITYLOCATION, new ArrayList<>(activityLocationList));
        measurementDetailFragment.setArguments(bundle);

        return measurementDetailFragment;
    }

    @Override
    public void showInVehicleDetails(long distance, long timeInMilis) {

    }

    @Override
    public void showOnBicycleDetails(long distance, long timeInMilis) {
        float distanceInKm = distance / 1000f;
        float timeInHour = distanceInKm > 0 ? timeInMilis / 1000f / 60f / 60f : 0;
        float averageSpeed = distanceInKm > 0 && timeInHour > 0 ? distanceInKm / timeInHour : 0;
        textViewBicycleDistance.setText(String.format(getString(R.string.measurementdetailfragment_measuredistance), distanceInKm));
        textViewBicycleTime.setText(String.format(getString(R.string.measurementdetailfragment_measuretime), timeInHour));
        textViewBicycleAverageSpeed.setText(String.format(getString(R.string.measurementdetailfragment_measureaveragespeed), averageSpeed));
    }

    @Override
    public void showOnFootDetails(long distance, long timeInMilis) {

    }

    @Override
    public void showRunningDetails(long distance, long timeInMilis) {

    }

}
