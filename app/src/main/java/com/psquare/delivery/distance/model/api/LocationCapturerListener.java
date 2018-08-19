package com.psquare.delivery.distance.model.api;

import com.psquare.delivery.distance.model.entity.Location;

public interface LocationCapturerListener {
    void connectionFailed(String errorMessage);

    void onLocationCaptured(Location location);
}
