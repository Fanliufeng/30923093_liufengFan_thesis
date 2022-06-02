package com.liufeng.contextcollectionapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.liufeng.contextcollectionapp.dao.LocationDAO;
import com.liufeng.contextcollectionapp.entity.Location;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDAO locationDAO();
    private static LocationDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriterExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized LocationDatabase getINSTANCE(final Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    LocationDatabase.class, "LocationDatabase")
                    .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }


}
