package com.psquare.delivery.distance.presenter;


import com.psquare.delivery.distance.model.Measurer;
import com.psquare.delivery.distance.model.entity.ActivityLocation;
import com.psquare.delivery.distance.model.entity.ActivityType;
import com.psquare.delivery.distance.view.MeasurementDetailView;

import java.util.List;

public class MeasurementDetailPresenter {

    private MeasurementDetailView view;

    public MeasurementDetailPresenter(MeasurementDetailView view) {
        this.view = view;
    }

    public void init(List<ActivityLocation> activityLocationList) {
        Measurer measurer = new Measurer(activityLocationList);
        view.showOnBicycleDetails(measurer.getDistance(ActivityType.ON_BICYCLE), measurer.getTime(ActivityType.ON_BICYCLE));
    }
}
