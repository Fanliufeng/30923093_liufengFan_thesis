package com.liufeng.contextcollectionapp.object;

public class ContextInfo {
    public String timeStamp;
    public String lat;
    public String lon;
    public double speed;
    public String netStatus;
    public String SSID;
    public String BSSID;
    public String RSSI;
    public String batteryStatus;
    public String screenStatus;
    public String locationName;

    public ContextInfo(String timeStamp, String lat, String lon,
                       double speed, String netStatus, String SSID,
                       String BSSID, String RSSI, String batteryStatus,
                       String screenStatus, String locationName) {
        this.timeStamp = timeStamp;
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.netStatus = netStatus;
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.RSSI = RSSI;
        this.batteryStatus = batteryStatus;
        this.screenStatus = screenStatus;
        this.locationName = locationName;
    }

    public ContextInfo() {
        this.timeStamp = null;
        this.lat = null;
        this.lon = null;
        this.speed = 0;
        this.netStatus = null;
        this.SSID = null;
        this.BSSID = null;
        this.RSSI = null;
        this.batteryStatus = null;
        this.screenStatus = null;
        this.locationName = null;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(String netStatus) {
        this.netStatus = netStatus;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getScreenStatus() {
        return screenStatus;
    }

    public void setScreenStatus(String screenStatus) {
        this.screenStatus = screenStatus;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
