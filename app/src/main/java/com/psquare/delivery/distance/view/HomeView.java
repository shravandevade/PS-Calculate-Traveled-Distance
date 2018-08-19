package com.psquare.delivery.distance.view;

import com.psquare.delivery.distance.model.entity.ActivityType;
import com.psquare.delivery.distance.model.entity.Location;

public interface HomeView {
    void warnWasNotPossibleToCaptureLocation(String errorMessage);

    void show(Location location, ActivityType activityType);

    void warnWasNotPossibleToRecognizeActivity(String errorMessage);

    void warnTracking();

    void warnTrackingHasBeenStopped();

    void showStopButton();

    void showPlayButton();
}
