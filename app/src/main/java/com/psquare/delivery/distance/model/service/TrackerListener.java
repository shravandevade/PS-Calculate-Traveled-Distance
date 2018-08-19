package com.psquare.delivery.distance.model.service;

import com.psquare.delivery.distance.model.entity.ActivityType;
import com.psquare.delivery.distance.model.entity.Location;

public interface TrackerListener {
    void onTracked(ActivityType activityType, Location location);
}
