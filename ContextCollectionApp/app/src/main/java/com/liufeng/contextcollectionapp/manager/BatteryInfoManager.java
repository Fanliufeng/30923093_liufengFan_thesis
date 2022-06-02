package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



/**
 * edited by liufeng Fan
 * this class is used to get battery info, charging status
 */
public class BatteryInfoManager {

    private Context mContext = null;
    //private BatteryListener listener = null;

    private static final long MIN_TIME_BW_UPDATES = 1000  * 1; // 1 second

    // flag if battery service valid or not
    private Boolean supported;

    public boolean isCharging;
    public float batteryLevel;

    private boolean running = false;

    /**
     * Returns true if the manager is listening to orientation chages
     */
    public boolean isListening(){
        return running;
    }

    /**
     * Unregisters listeners
     */
    public void stopListener(){
        running = false;
    }

    public BatteryInfoManager(Context context){
        this.mContext = context;
        getBatteryInfo();

    }
    /**
     * Return true if the battery service work
     */
    public boolean isSupported(){

        if(supported == null){
            if(mContext != null){
                supported = Boolean.TRUE;
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BATTERY_STATS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BATTERY_STATS},0);
                }

            }else{
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    /**
     * Registers a listener and start listening
     */
    public void startListening(){
        //listener = batteryListener;
        running = true;
        //scheduleReadBatteryInfos();
    }

    /*
    public void scheduleReadBatteryInfos() {
        if(mContext != null) {
            Context appContext = mContext.getApplicationContext();
            AlarmManager scheduler = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(appContext, BatteryInfoBroadcastReceiver.class);
            PendingIntent scheduleBatteryInfoIntent = PendingIntent.getBroadcast(appContext, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            scheduler.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MIN_TIME_BW_UPDATES, scheduleBatteryInfoIntent);
        }
    }
*/
    /**
     * Get Battery information
     */
    public void getBatteryInfo(){
        if (isSupported()){
            startListening();
            if(isListening()) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = mContext.registerReceiver(null, ifilter);

                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryLevel = level / (float) scale;
            }
        }
    }

    public boolean isBatteryCharging(){
        return isCharging;
    }

    public float getBatteryLevel(){
        return batteryLevel;
    }
}
