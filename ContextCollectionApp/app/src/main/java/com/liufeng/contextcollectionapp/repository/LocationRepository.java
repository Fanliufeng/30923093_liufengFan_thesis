package com.liufeng.contextcollectionapp.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.liufeng.contextcollectionapp.dao.LocationDAO;
import com.liufeng.contextcollectionapp.database.LocationDatabase;
import com.liufeng.contextcollectionapp.entity.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class LocationRepository {
    private LocationDAO locationDAO;
    private LiveData<List<Location>> allLocations;

    public LocationRepository(Application application){
        LocationDatabase db = LocationDatabase.getINSTANCE(application);
        locationDAO = db.locationDAO();
        allLocations = locationDAO.getALL();
    }
    // Room executes this query on a separate thread
    public LiveData<List<Location>> getAllLocations(){
        return allLocations;
    }

    public void insert(final Location location){
        LocationDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                locationDAO.insert(location);
            }
        });
    }

    public void deleteAll(){
        LocationDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                locationDAO.deleteAll();
            }
        });
    }

    public void delete(final Location location){
        LocationDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                locationDAO.delete(location);
            }
        });
    }

    public void updateLocation(final Location location){
        LocationDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                locationDAO.updateCustomer(location);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Location> findByIDFuture(final int locationId){
        return CompletableFuture.supplyAsync(new Supplier<Location>() {
            @Override
            public Location get() {
                return locationDAO.findByID(locationId);
            }
        }, LocationDatabase.databaseWriterExecutor);
    }
}
