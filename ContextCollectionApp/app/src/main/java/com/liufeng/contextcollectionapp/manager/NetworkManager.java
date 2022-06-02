package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * edited by liufeng Fan
 * this class is used to get network status
 */
public class NetworkManager extends Service implements ConnectivityManager.OnNetworkActiveListener {
    private final Context mContext;
    // flag for network status
    boolean networkConnected = false;

    boolean wifiConnected = false;
    boolean mobileConnected = false;

    // Declaring a network Manager
    protected ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    public NetworkManager(Context context){
        this.mContext = context;
        getNetworkType();
    }

    /**
     * Get network status, information
     * @return
     */
    public NetworkInfo getNetworkType(){
        try {
            connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 0);
            }
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_WIFI_STATE},0);
            }
            networkInfo = connectivityManager.getActiveNetworkInfo();
            networkConnected = networkInfo.isConnected();


            if (networkInfo != null && networkInfo.isConnected()){ //connected with either mobile or wifi
                wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }

        }catch (Exception e){
            e.printStackTrace();

        }
        return networkInfo;
    }

    /**
     * function to get network Status
     */

    public boolean getNetworkStatus(){
        return networkConnected;
    }

    /**
     * function to get network Status
     */

    public boolean getWifiStatus(){
        return wifiConnected;
    }

    /**
     * function to get network Status
     */

    public boolean getMobileStatus(){
        return mobileConnected;
    }



    @Override
    public void onNetworkActive(){
        getNetworkType();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
