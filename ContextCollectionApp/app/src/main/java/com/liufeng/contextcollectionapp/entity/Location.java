package com.liufeng.contextcollectionapp.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "Location_name")
    @NonNull
    public String loc_name;

    @ColumnInfo(name = "Latitude")
    @NonNull
    public double loc_lat;

    @ColumnInfo(name = "Longitude")
    @NonNull
    public double loc_lon;

    @ColumnInfo(name = "BSSID")
    public String loc_bssid;

    @ColumnInfo(name = "SSID")
    public String loc_ssid;

    public Location(@NonNull String name, @NonNull double latitude, @NonNull double longitude, String bssid, String ssid){
        this.loc_name = name;
        this.loc_lat = latitude;
        this.loc_lon = longitude;
        this.loc_bssid = bssid;
        this.loc_ssid = ssid;
    }

    public Location(){

    }

}
