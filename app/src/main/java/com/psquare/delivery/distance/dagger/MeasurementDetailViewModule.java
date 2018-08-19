package com.psquare.delivery.distance.dagger;

import com.psquare.delivery.distance.presenter.MeasurementDetailPresenter;
import com.psquare.delivery.distance.view.MeasurementDetailView;
import com.psquare.delivery.distance.view.fragment.MeasurementDetailFragment;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class}, injects = MeasurementDetailFragment.class)
public class MeasurementDetailViewModule {

    private MeasurementDetailView view;

    public MeasurementDetailViewModule(MeasurementDetailFragment view) {
        this.view = view;
    }

    @Provides
    public MeasurementDetailPresenter provideMeasurementDetailPresenter() {
        return new MeasurementDetailPresenter(view);
    }
}
