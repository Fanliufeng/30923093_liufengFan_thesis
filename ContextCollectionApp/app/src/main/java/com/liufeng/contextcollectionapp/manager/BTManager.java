package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class BTManager {
    private Context mContext;

    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public boolean isBluetoothConnected = false;
    public String deviceName = null;
    public String deviceHardwareAddress;

    public BTManager(Context context) {
        mContext = context;
    }

    public boolean checkBTStatus() {
        BluetoothManager bm = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();
                deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
        return isBluetoothConnected;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceHardwareAddress() {
        return deviceHardwareAddress;
    }


    /**
     * 判断给定的设备mac地址是否已连接经典蓝牙
     *
     * @param macAddress 设备mac地址,例如"78:02:B7:01:01:16"
     * @return
     */
    public boolean isConnectClassicBT(String macAddress) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
        }
        if (TextUtils.isEmpty(macAddress)) {
            return false;
        }
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//Get BluetoothAdapter Class object
        try {
            //Check if BT devices connected
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //Get permission
            method.setAccessible(true);
            int state = (int) method.invoke(bluetoothAdapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.d("test", "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                Log.d("test", "devices:" + devices.size());
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        {
                            Log.d("mac", device.getAddress());
                            return macAddress.contains(device.getAddress());
                        }
                    } else {
                        Log.d("test", device.getName() + " connect false(" + device.getAddress() + ")");
                    }
                }

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
