package com.psquare.delivery.distance.dagger;

import com.psquare.delivery.DeliveryMainActivity;
import com.psquare.delivery.distance.model.service.Tracker;
import com.psquare.delivery.distance.presenter.HomePresenter;
import com.psquare.delivery.distance.view.HomeView;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {AppModule.class, ApiModule.class, DaoModule.class}, injects = DeliveryMainActivity.class)
public class HomeViewModule {

    private HomeView view;

    public HomeViewModule(HomeView view) {
        this.view = view;
    }

    @Provides
    public HomePresenter provideHomePresenter(Tracker tracker) {
        return new HomePresenter(view, tracker);
    }
}
