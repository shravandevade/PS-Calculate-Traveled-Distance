package com.psquare.delivery.distance.model.dao;

import com.psquare.delivery.distance.model.entity.ActivityLocation;

import java.util.Date;
import java.util.List;

public interface ActivityLocationDao {
    boolean insert(ActivityLocation activityLocation);

    List<ActivityLocation> listAll(Date currentDay);

    List<ActivityLocation> listAll(Date startDate, Date finalDate);
}
