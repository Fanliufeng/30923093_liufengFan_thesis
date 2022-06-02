package com.liufeng.contextcollectionapp.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.liufeng.contextcollectionapp.entity.Location;
import com.liufeng.contextcollectionapp.repository.LocationRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocationViewModel extends AndroidViewModel {
    private LocationRepository locationRepository;
    private LiveData<List<Location>> allLocations;

    public LocationViewModel(Application application){
        super(application);
        locationRepository = new LocationRepository(application);
        allLocations = locationRepository.getAllLocations();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<Location> findByIdFuture(final int locationId){
        return locationRepository.findByIDFuture(locationId);
    }

    public LiveData<List<Location>> getAllLocations(){
        return allLocations;
    }

    public void insert(Location location){
        locationRepository.insert(location);
    }

    public void deleteAll(){
        locationRepository.deleteAll();
    }

    public void update(Location location){
        locationRepository.updateLocation(location);
    }

}
