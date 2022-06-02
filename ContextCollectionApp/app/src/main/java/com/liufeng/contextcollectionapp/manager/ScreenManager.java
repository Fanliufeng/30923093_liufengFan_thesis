package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * edited by liufeng Fan
 * this class is used to get screen status
 */

public class ScreenManager {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;
    public boolean isScreenON = true;

    /**
     * constructor
     * @param context
     */
    public ScreenManager(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }


    public void startObserver(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();

    }

    public void shutdownObserver() {
        unregisterListener();
    }

    /**
     * Get screen status
     */
    public boolean getScreenStatus(){
        registerListener();
        return isScreenON;
    }



    /**
     * Register screen service
     */
    private void registerListener() {
        if (mContext != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(mScreenReceiver, filter);
        }
    }

    /**
     * Unregister screen service
     */
    private void unregisterListener() {
        if (mContext != null)
            mContext.unregisterReceiver(mScreenReceiver);
    }


    /**
     * Receive screen status
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            // Screen on
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                isScreenON = true;
                //mScreenStateListener.onScreenOn();
            // Screen locked
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                isScreenON =false;
                //mScreenStateListener.onScreenOff();
            // Screen on
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                //mScreenStateListener.onUserPresent();
                isScreenON = true;
            }
        }
    }


    /**
     * Return screen status
     */
    public interface ScreenStateListener {
        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();
    }
}
