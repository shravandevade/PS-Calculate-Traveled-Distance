package com.psquare.delivery.distance.dagger;

import android.content.Context;

import com.psquare.delivery.distance.model.api.ActivityRecognizer;
import com.psquare.delivery.distance.model.api.LocationCapturer;
import com.psquare.delivery.distance.model.api.impl.ActivityRecognizerImpl;
import com.psquare.delivery.distance.model.api.impl.LocationCapturerImpl;
import com.psquare.delivery.distance.model.service.Tracker;
import com.psquare.delivery.distance.model.service.TrackerImpl;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AppModule.class)
public class ApiModule {

    @Provides
    public ActivityRecognizer provideActivityRecognizer(Context context) {
        return new ActivityRecognizerImpl(context);
    }

    @Provides
    public LocationCapturer provideLocationCapturer(Context context) {
        return new LocationCapturerImpl(context);
    }

    @Provides
    public Tracker provideTracker(Context context) {
        return new TrackerImpl(context);
    }

}
