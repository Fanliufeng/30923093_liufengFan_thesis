package com.liufeng.contextcollectionapp.object;

public class NoiseInfo {
    public String timeStamp;
    public double noiseLevel;

    public NoiseInfo(String timeStamp, double noiseLevel) {
        this.timeStamp = timeStamp;
        this.noiseLevel = noiseLevel;
    }
    public NoiseInfo(){

    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getNoiseLevel() {
        return noiseLevel;
    }

    public void setNoiseLevel(double noiseLevel) {
        this.noiseLevel = noiseLevel;
    }
}
