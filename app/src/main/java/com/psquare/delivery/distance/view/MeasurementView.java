package com.psquare.delivery.distance.view;

import com.psquare.delivery.distance.model.entity.ActivityLocation;
import java.util.List;

public interface MeasurementView {
    void showDetailsOfActivityLocation(List<ActivityLocation> activityLocationListOfToday,
                                       List<ActivityLocation> activityLocationListOfWeek);
}
