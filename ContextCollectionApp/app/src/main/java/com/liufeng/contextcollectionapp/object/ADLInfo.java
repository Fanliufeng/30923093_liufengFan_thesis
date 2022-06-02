package com.liufeng.contextcollectionapp.object;

public class ADLInfo {
    public String timeStamp;
    public String ADL;

    public ADLInfo(String timeStamp, String ADL) {
        this.timeStamp = timeStamp;
        this.ADL = ADL;
    }

    public ADLInfo() {
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getADL() {
        return ADL;
    }

    public void setADL(String ADL) {
        this.ADL = ADL;
    }
}
