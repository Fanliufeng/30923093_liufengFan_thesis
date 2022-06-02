package com.liufeng.contextcollectionapp.manager;



/**
 * edited by liufeng Fan
 * this class is used to store wifi information
 */
public class WiFiInfo {
    int networkId;
    // Name of wifi
    public String ssid;
    // BSSID
    public String bssid;
    // Signal strength
    public int rssi;


    public WiFiInfo(int networkId, String ssid, String bssid, int rssi){
        this.networkId = networkId;
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;

    }

    public WiFiInfo(){
        this.networkId = 0;
        this.ssid = null;
        this.bssid = null;
        this.rssi = 0;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}
