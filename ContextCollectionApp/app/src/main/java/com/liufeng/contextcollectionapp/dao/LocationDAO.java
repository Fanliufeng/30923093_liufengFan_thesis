package com.liufeng.contextcollectionapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.liufeng.contextcollectionapp.entity.Location;

import java.util.List;

@Dao
public interface LocationDAO {

    @Query("SELECT * FROM Location ORDER BY uid")
    LiveData<List<Location>> getALL();

    @Query("SELECT * FROM Location WHERE uid = :locationId LIMIT 1")
    Location findByID(int locationId);

    @Insert
    void insert(Location location);

    @Delete
    void delete(Location location);

    @Update
    void updateCustomer(Location location);

    @Query("DELETE FROM location")
    void deleteAll();
}
