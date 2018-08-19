package com.psquare.delivery.distance.model.api;

import com.psquare.delivery.distance.model.entity.ActivityType;

public interface ActivityRecognizerListener {
    void connectionFailed(String errorMessage);

    void onActivityRecognized(ActivityType activityType);
}
