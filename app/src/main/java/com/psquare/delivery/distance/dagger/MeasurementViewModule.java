package com.psquare.delivery.distance.dagger;

import com.psquare.delivery.distance.model.dao.ActivityLocationDao;
import com.psquare.delivery.distance.presenter.MeasurementPresenter;
import com.psquare.delivery.distance.view.MeasurementView;
import com.psquare.delivery.distance.view.activity.MeasurementActivity;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class, DaoModule.class}, injects = MeasurementActivity.class)
public class MeasurementViewModule {

    MeasurementView view;

    public MeasurementViewModule(MeasurementView view) {
        this.view = view;
    }

    @Provides
    public MeasurementPresenter provideMeasurementPresenter(ActivityLocationDao activityLocationDao) {
        return new MeasurementPresenter(view, activityLocationDao);
    }

}
