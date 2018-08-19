package com.psquare.delivery.distance.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.psquare.delivery.ApplicationManager;
import com.psquare.delivery.R;
import com.psquare.delivery.distance.GetDistanceApplication;
import com.psquare.delivery.distance.dagger.MeasurementViewModule;
import com.psquare.delivery.distance.model.entity.ActivityLocation;
import com.psquare.delivery.distance.presenter.MeasurementPresenter;
import com.psquare.delivery.distance.view.MeasurementView;
import com.psquare.delivery.distance.view.fragment.MeasurementDetailFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MeasurementActivity extends AppCompatActivity implements MeasurementView {

    @Inject
    MeasurementPresenter presenter;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        ((ApplicationManager) getApplication()).getObjectGraph().plus(new MeasurementViewModule(this)).inject(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter.init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDetailsOfActivityLocation(final List<ActivityLocation> activityLocationListOfToday, final List<ActivityLocation> activityLocationListOfWeek) {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return MeasurementDetailFragment.newInstance(activityLocationListOfToday);
                    case 1:
                        return MeasurementDetailFragment.newInstance(activityLocationListOfWeek);
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.measurementactivity_today);
                    case 1:
                        return getString(R.string.measurementactivity_week);
                }
                return null;
            }
        });
    }

    public static final Intent newInstance(Context context) {
        Intent intent = new Intent(context, MeasurementActivity.class);

        return intent;
    }
}
