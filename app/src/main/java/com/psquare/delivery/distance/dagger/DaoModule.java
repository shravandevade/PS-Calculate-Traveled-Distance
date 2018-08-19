package com.psquare.delivery.distance.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.psquare.delivery.distance.model.dao.ActivityLocationDao;
import com.psquare.delivery.distance.model.dao.SqliteConnection;
import com.psquare.delivery.distance.model.dao.impl.ActivityLocationDaoImpl;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = AppModule.class)
public class DaoModule {

    @Provides
    public SQLiteDatabase provideSqLiteDatabase(Context context) {
        return new SqliteConnection(context).getWritableDatabase();
    }

    @Provides
    public ActivityLocationDao provideActivityLocationDao(Context context) {
        return new ActivityLocationDaoImpl(provideSqLiteDatabase(context));
    }

}
