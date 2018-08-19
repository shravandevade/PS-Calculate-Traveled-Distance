package com.psquare.delivery.distance;

import android.app.Application;

import com.psquare.delivery.distance.dagger.ApiModule;
import com.psquare.delivery.distance.dagger.AppModule;
import com.psquare.delivery.distance.dagger.DaoModule;
import dagger.ObjectGraph;

public class GetDistanceApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(
                new Object[]{
                        new AppModule(GetDistanceApplication.this),
                        new ApiModule(),
                        new DaoModule()
                }
        );
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }
}
