package com.psquare.delivery.distance.model.api;

public interface LocationCapturer {

    void stopToCaptureLocations();

    void startToCaptureLocations();

    void setLocationCapturerListener(LocationCapturerListener listener);

}
