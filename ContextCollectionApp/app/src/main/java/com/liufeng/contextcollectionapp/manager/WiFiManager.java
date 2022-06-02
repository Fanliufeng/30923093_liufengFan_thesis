package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

/**
 * edited by liufeng Fan
 * this class is used to get wifi information
 */
public class WiFiManager {

    private Context mContext = null;

    private WifiManager wifiManager;

    private WiFiInfo newWifiInfo = new WiFiInfo();

    //private static Long MIN_TIME_TO_UPDATE_MILLISEC = null;

    // flag support wifi
    public boolean supported = false;

    /**
     * indicates whether or not WIFI is running
     */
    private boolean running = false;

    public WiFiManager(Context context){
        this.mContext = context;
        getNewWifiInfo();

    }

    /**
     * Returns true if the manager is listening to orientation changes
     */
    public boolean isListening() {
        return running;
    }

    /**
     * Unregisters listeners
     */
    public  void stopListening() {
        running = false;
        //MIN_TIME_TO_UPDATE_MILLISEC = null;
        wifiManager = null;

    }

    /**
     * Returns true if at least one Barometer sensor is available
     */
    public boolean isSupported() {

        if (mContext != null) {
            if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
                return false;
            }

            wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_WIFI_STATE},0);
            }
            if (wifiManager == null)
                return false;

            if (wifiManager.isWifiEnabled() == false) {
                wifiManager.setWifiEnabled(true);
                running = true;
            }

            running = true;
            return true;
        }

        return false;
    }


    public WiFiInfo getNewWifiInfo() {

        if (isSupported()) {
            if ( wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int NetworkId = wifiInfo.getNetworkId();
                String ssid = wifiInfo.getSSID();
                String bssid = wifiInfo.getBSSID();
                int rssi = wifiInfo.getRssi();
                newWifiInfo.setBssid(bssid);
                newWifiInfo.setNetworkId(NetworkId);
                newWifiInfo.setSsid(ssid);
                newWifiInfo.setRssi(rssi);
            }
        }
        return newWifiInfo;
    }
}

