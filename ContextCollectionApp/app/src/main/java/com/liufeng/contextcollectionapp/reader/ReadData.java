package com.liufeng.contextcollectionapp.reader;

import android.content.Context;
import android.util.Log;

import com.liufeng.contextcollectionapp.object.ADLInfo;
import com.liufeng.contextcollectionapp.object.ContextInfo;
import com.liufeng.contextcollectionapp.object.NoiseInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadData {
    private Context mContext;
    public ArrayList<ContextInfo> contextInfoList;
    public ArrayList<String> timeStampList;
    public ArrayList<String> latList, lonList, screenStatusList, locationNameList, BTStatusList;
    public ArrayList<NoiseInfo> noiseInfoList;
    public ArrayList<ADLInfo> adlInfoList;
    public ArrayList<String> ADLList;
    public ArrayList<Double> noiseLevelList,speedList;
    public String contextFile, noiseFile, adlFile;


    public ReadData(Context context) {
        mContext = context;
        noiseFile = mContext.getFilesDir() + "/" + "noise_raw_data5.csv";
        contextFile = mContext.getFilesDir() + "/" + "other_context_data5.csv";
        adlFile = mContext.getFilesDir() +"/" + "Activity_Predict5.csv";
        contextInfoList = new ArrayList<>();
        timeStampList = new ArrayList<>();
        latList = new ArrayList<>();
        lonList = new ArrayList<>();
        noiseLevelList = new ArrayList<>();
        noiseInfoList = new ArrayList<>();
        ADLList = new ArrayList<>();
        adlInfoList = new ArrayList<>();
        locationNameList = new ArrayList<>();
        speedList = new ArrayList<>();
        screenStatusList = new ArrayList<>();
        BTStatusList = new ArrayList<>();
    }


    public void readContextData(){
        try (BufferedReader br = new BufferedReader(new FileReader(contextFile))){
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\"", "");
                String[] values = line.split(",");
                String timeStamp = values[0];
                String lat = values[1];
                String lon = values[2];
                double speed = Double.parseDouble(values[3]);
                String netStatus = values[4];
                String SSID = values[5];
                String BSSID = values[6];
                String RSSI = values[7];
                String batteryStatus = values[8];
                String screenStatus = values[9];
                String locationName = values[10];
                String BTStatus = values[13];
                ContextInfo newContext = new ContextInfo(timeStamp,lat,lon,speed,netStatus,
                        SSID,BSSID,RSSI,batteryStatus,screenStatus,locationName);
                contextInfoList.add(newContext);
                lonList.add(lon);
                latList.add(lat);
                timeStampList.add(timeStamp);
                speedList.add(speed);
                screenStatusList.add(screenStatus);
                locationNameList.add(locationName);
                BTStatusList.add(BTStatus);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readADLData(){
        try (BufferedReader br = new BufferedReader(new FileReader(adlFile))){
            String line;
            while ((line = br.readLine()) != null) {
                //Log.e("ADL test: ", line);
                line = line.replaceAll("\"", "");
                //Log.e("ADL test: ", line);

                String[] values = line.split(",");
                //Log.e("ADL test: ", values[1]);

                String timeStamp = values[0];
                String ADL = values[1];

                ADLInfo newADL = new ADLInfo(timeStamp, ADL);
                //Log.e("ADL test: ", ADL);
                ADLList.add(ADL);
                adlInfoList.add(newADL);

            }

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public void readNoiseData(){
        try (BufferedReader br = new BufferedReader(new FileReader(noiseFile))){
            String line;
            while ((line = br.readLine()) != null) {
                //Log.e("Test Read Noise file", line);
                line = line.replaceAll("\"", "");
                String[] values = line.split(",");

                String timeStamp = values[0];
                //Log.e("Test Read Noise file 1", values[1]);
                double noiseLevel = Double.parseDouble(values[1]);
                NoiseInfo newNoise = new NoiseInfo(timeStamp, noiseLevel);
                noiseInfoList.add(newNoise);
                noiseLevelList.add(noiseLevel);
                //Log.e("Test Read Noise file", ""+noiseLevelList.size());
            }

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public ArrayList<ContextInfo> getContextInfoList() {
        return contextInfoList;
    }

    public void setContextInfoList(ArrayList<ContextInfo> contextInfoList) {
        this.contextInfoList = contextInfoList;
    }

    public ArrayList<String> getTimeStampList() {
        return timeStampList;
    }

    public void setTimeStampList(ArrayList<String> timeStampList) {
        this.timeStampList = timeStampList;
    }

    public ArrayList<String> getLatList() {
        return latList;
    }

    public void setLatList(ArrayList<String> latList) {
        this.latList = latList;
    }

    public ArrayList<String> getLonList() {
        return lonList;
    }

    public void setLonList(ArrayList<String> lonList) {
        this.lonList = lonList;
    }

    public ArrayList<String> getScreenStatusList() {
        return screenStatusList;
    }

    public void setScreenStatusList(ArrayList<String> screenStatusList) {
        this.screenStatusList = screenStatusList;
    }

    public ArrayList<String> getLocationNameList() {
        return locationNameList;
    }

    public void setLocationNameList(ArrayList<String> locationNameList) {
        this.locationNameList = locationNameList;
    }

    public ArrayList<NoiseInfo> getNoiseInfoList() {
        return noiseInfoList;
    }

    public void setNoiseInfoList(ArrayList<NoiseInfo> noiseInfoList) {
        this.noiseInfoList = noiseInfoList;
    }

    public ArrayList<ADLInfo> getAdlInfoList() {
        return adlInfoList;
    }

    public void setAdlInfoList(ArrayList<ADLInfo> adlInfoList) {
        this.adlInfoList = adlInfoList;
    }

    public ArrayList<String> getADLList() {
        return ADLList;
    }

    public void setADLList(ArrayList<String> ADLList) {
        this.ADLList = ADLList;
    }

    public ArrayList<Double> getNoiseLevelList() {
        return noiseLevelList;
    }

    public void setNoiseLevelList(ArrayList<Double> noiseLevelList) {
        this.noiseLevelList = noiseLevelList;
    }

    public ArrayList<Double> getSpeedList() {
        return speedList;
    }

    public void setSpeedList(ArrayList<Double> speedList) {
        this.speedList = speedList;
    }
}
