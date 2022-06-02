package com.liufeng.contextcollectionapp.caarmodel;

import static java.lang.Math.round;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.liufeng.contextcollectionapp.reader.ReadData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class RecognitionModel {
    public Context mContext;
    public ReadData mReadDta;
    private String[] ADL_list = {"LYING","WALKING", "WALKING_UPSTAIRS", "WALKING_DOWNSTAIRS", "SITTING", "STANDING"};


    public String contextInfo, ComplexADL, activityDuration;
    public RecognitionModel(Context context){
        this.mContext =  context;
        this.mReadDta = new ReadData(context);
        this.contextInfo = null;
        mReadDta.readADLData();
        mReadDta.readContextData();
        mReadDta.readNoiseData();
    }

    public String getSimpleARResult(){
        //mReadDta.readADLData();
        String ADL="None";
        float max_accuracy = 0;
        for (String temp: ADL_list){
            //Log.e("ADL", temp);
            int count = Collections.frequency(mReadDta.ADLList, temp);
            //Log.e(temp, ""+ count);
            //Log.e("Size", ""+ mReadDta.ADLList.size());
            float temp_accuracy = 100* count/mReadDta.ADLList.size();
            //Log.e(temp, ""+ temp_accuracy);
            if (temp_accuracy > max_accuracy){
                max_accuracy = temp_accuracy;
                ADL = temp;
            }
        }
        //Log.e("ADL", ADL);
        return ADL;
    }

    public double getAverageNoiseLevel(){
        double avgNoiseLevel = 0;
        //mReadDta.readNoiseData();
        double sumNoise = 0;
        ArrayList<Double> noiseLevelList = mReadDta.getNoiseLevelList();

        for(Double temp:noiseLevelList){
            sumNoise += temp;
        }
        avgNoiseLevel = sumNoise/noiseLevelList.size();
        Log.e("noise", avgNoiseLevel+"");
        return round(avgNoiseLevel);
    }


    public boolean checkCommuting(){
        boolean isCommuting = false;

        if(getMaxSpeed()> 15){
            isCommuting = true;
        }

        return isCommuting;
    }

    public boolean checkInHome(){
        //mReadDta.readContextData();
        boolean isHome = false;
        double homeCount = Collections.frequency(mReadDta.locationNameList, "home");
        //Log.d("size",mReadDta.locationNameList.size()+"" );
        //Log.d("count",homeCount+"" );
        double totalSize = mReadDta.locationNameList.size();
        //Log.d("size",totalSize+"" );

        double temp_home = homeCount/totalSize*100;
        //double temp_home = 722/721*100;
        //Log.d("size",temp_home+"" );
        if (temp_home > 90)
            isHome = true;

        return isHome;
    }

    public boolean checkDayTime(){
        //mReadDta.readContextData();
        boolean isDayTime = false;
        String startTime = mReadDta.timeStampList.get(0);
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Long startTimeL = Long.parseLong(startTime);

        String startHour = formatter.format(startTimeL);
        int startHourInt = Integer.parseInt(startHour);
        //Log.e("Check Time", startHour);

        if (startHourInt >= 7 && startHourInt <= 20){
            isDayTime = true;
        }

        return isDayTime;
    }

    public boolean checkDuration(){
        //mReadDta.readContextData();
        boolean isEnoughDuration = false;
        int arraySize = mReadDta.timeStampList.size();
        String startTime = mReadDta.timeStampList.get(0);
        String endTime = mReadDta.timeStampList.get(arraySize-1);

        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        Log.e("Check Time", startTime);
        Long startTimeL = Long.parseLong(startTime);
        Long endTimeL = Long.parseLong(endTime);

        long timeDifference = (endTimeL-startTimeL)/(60*1000);

        activityDuration = String.valueOf(timeDifference);
        //Log.e("Check duration", String.valueOf(timeDifference));
        if (timeDifference >= 30){
            isEnoughDuration = true;
        }

        return isEnoughDuration;
    }

    public boolean checkScreenOn(){
        //mReadDta.readContextData();
        boolean isScreenOn = true;

        int count = Collections.frequency(mReadDta.screenStatusList, "Off");
        float screen_off_rate = count/mReadDta.screenStatusList.size();

        if (screen_off_rate > 0.9){
            isScreenOn = false;
        }

        return isScreenOn;
    }

    public double getAverageSpeed(){
        double avgSpeed = 0;
        double sumSpeed = 0;
        ArrayList<Double> speedList = mReadDta.speedList;

        for (Double temp: speedList){
            sumSpeed = sumSpeed + temp;
        }
        avgSpeed = sumSpeed/speedList.size();

        return round(avgSpeed);
    }
    /**
     * get max speed
     * @return
     */
    public double getMaxSpeed(){
        double speed = 0;
        speed= Collections.max(mReadDta.speedList);
        return round(speed);
    }
    public String getAllScenario(){
        String scenario = getSimpleARResult();

        if(checkInHome()){
            ComplexADL = "Indoor Activity";
            scenario = recognizeHomeActivity();
        }else if(checkCommuting()){
            ComplexADL = "Outdoor Activities";
            scenario = recognizeCommutingActivity();
        }else{
            ComplexADL = "Outdoor Activities";
            scenario = "Outdoor Activities";
        }
        return scenario;
    }

    public String recognizeCommutingActivity(){
        String simpleADL = getSimpleARResult();
        String scenario = "Commuting";
        if (getSimpleARResult().equalsIgnoreCase("STANDING")||
                getSimpleARResult().equalsIgnoreCase("SITTING")||
                getSimpleARResult().equalsIgnoreCase("LYING")
        ){
            contextInfo = "Location: Not Home.\n" +
                    "Physical activity: " + simpleADL+ "\n" +
                    "Max Speed: " + getMaxSpeed() + " KM/H \n" +
                    "Average Speed: " + getAverageSpeed()+ " KM/H \n" +
                    "Bluetooth status: None\n\n" +
                    "You are recognized as " + simpleADL+", but speed is not zero. Therefore, "+
                    "you are in commuting now, and there is no a carplay BT device connected." +
                    "Based on above context, recognize activity as \"Commuting by public transport\"";
            scenario = "Commuting by public transport";

            if(getBTStatus().equalsIgnoreCase("Carplay on")){
                contextInfo = "Location: Not Home.\n" +
                        "Physical activity: " + simpleADL+ "\n" +
                        "Max Speed: " + getMaxSpeed() + " KM/H \n" +
                        "Average Speed: " + getAverageSpeed()+ " KM/H \n" +
                        "Bluetooth status: Carplay on\n\n" +
                        "You are recognized as " + simpleADL+", but speed is not zero. Therefore, "+
                        "you are in commuting now, and there is a carplay BT device connected." +
                        "Based on above context, recognize activity as \"Commuting by private vehicle\"";
                scenario = "Commuting by private vehicle";
            }
        }else{
            contextInfo = "Location: Not Home.\n" +
                    "Physical activity: " + simpleADL +".\n"+
                    "Max Speed: " + getMaxSpeed() + " KM/H \n" +
                    "Average Speed: " + getAverageSpeed()+ " KM/H \n" +
                    "Bluetooth status: None\n\n"+
                    "Speed is so high. People is commuting, the physical activity recognition should not be " + simpleADL+
                    "." + "Therefore, the physical recognition may wrong! " +
                    "Based on above context, recognize activity as \"Commuting by public transport\" ";
            scenario = "Commuting by public transport";

            if(getBTStatus().equalsIgnoreCase("Carplay on")){
                contextInfo = "Location: Not Home.\n" +
                        "Physical activity: " + simpleADL +".\n"+
                        "Max Speed: " + getMaxSpeed() + " KM/H \n" +
                        "Average Speed: " + getAverageSpeed()+ " KM/H \n" +
                        "Bluetooth status: None\n\n"+
                        "Speed is so high. People is commuting, the physical activity recognition should not be " + simpleADL+
                        "." + "Therefore, the physical recognition may wrong! " +
                        "you are in commuting now, and there is a carplay BT device connected." +
                        "Based on above context, recognize activity as \"Commuting by private vehicle\"";
                scenario = "Commuting by private vehicle";
            }
        }
        return scenario;
    }
    
    public String recognizeHomeActivity(){
        String scenario = getSimpleARResult();
        if (getSimpleARResult().equalsIgnoreCase("LYING")) {
            if (!checkDuration()) {
                scenario = "Home Activities";
            }else{
                if(checkScreenOn()){
                    scenario = "Home Activities";
                }else{
                    if(checkDayTime()){
                        scenario = "Home Activities";
                        if(getBTStatus().equalsIgnoreCase("Laptop on")){
                            scenario = "Studying (Home Activities)";
                        }
                    }
                }
            }
        }else{
            scenario = "Home Activities";
        }
        if(isSleeping()){
            scenario = "Home activities (Sleeping)";

        }

        return scenario;
    }

    public String getBTStatus(){
        String BTStatus = "None";
        ArrayList<String> BTStatusList = mReadDta.BTStatusList;
        String [] Status = {"Laptop on", "Carplay on", "None"};
        int maxCount = 0;
        for(String temp: Status){
            int count = Collections.frequency(BTStatusList, temp);
            if(count>maxCount){
                maxCount = count;
                BTStatus = temp;
            }
        }
        return BTStatus;
    }

    public boolean isSleeping(){
        boolean sleepingStatus = false;
        if(!isPhoneOnUser() && !checkDayTime()){
            sleepingStatus = true;
        }
        return sleepingStatus;
    }

    /**
     * Check is phone on user, using activity recognition and screen status and duration
     * @return boolean type
     */
    public boolean isPhoneOnUser(){
        boolean phoneOnUser = true;
        if(getSimpleARResult().equalsIgnoreCase("LYING")
                && checkDuration() && !checkScreenOn()){
            phoneOnUser = false;
        }
        return phoneOnUser;
    }

    /**
     * Check sound level
     * @return boolean quiet for true
     */
    public boolean isQuiet(){
        if (getAverageNoiseLevel() < 40){
            return true;
        }
        else {
            return false;
        }
    }
    public String getScenario(){
        String scenario = getSimpleARResult();
        if (scenario.equalsIgnoreCase("LYING")){
            if(checkDuration()){
                if(checkInHome()){
                    if(checkScreenOn()) {
                        scenario = "Recognized physical activity: Lying \n" +
                                "Location: Home. \n" +
                                "Phone is on user: Yes \n" +
                                "Recognized ADL: Phone is using";;
                    }
                    else {
                        if (checkDayTime()) {
                            if (getAverageNoiseLevel() < 40) {
                                scenario = "Recognized physical activity: Lying \n" +
                                        "Location: Home. \n" +
                                        "Phone is on user: No \n" +
                                        "Surrounding sound: Quiet \n" +
                                        "Time: Daytime. \n" +
                                        "Recognized ADL: Study or work at home";
                            }
                            else{
                                scenario = "Recognized physical activity: Lying \n" +
                                        "Location: Home. \n" +
                                        "Phone is on user: No \n" +
                                        "Surrounding sound: High level \n" +
                                        "Time: Daytime. \n" +
                                        "Recognized ADL: Home activities, such as cooking. ";
                            }

                        }
                        else {

                            if (getAverageNoiseLevel() < 40) {
                                scenario = "Recognized physical activity: Lying \n" +
                                        "Location: Home. \n" +
                                        "Phone is on user: No \n" +
                                        "Surrounding sound: Quiet \n" +
                                        "Time: Night. \n" +
                                        "Recognized ADL: User is sleeping now \n";
                            } else {
                                scenario ="Recognized physical activity: Lying \n" +
                                        "Location: Home. \n" +
                                        "Phone is on user: No \n" +
                                        "Surrounding sound: High level \n" +
                                        "Time: Night. \n" +
                                        "Recognized ADL: some entertainment at home. ";
                            }
                        }
                    }


                }
                else{
                    scenario = "Recognized physical activity: Lying \n"+
                            "Location: Not Home \n" +
                            "Recognized ADL: The phone may leave in somewhere";
                }

            }
            else{
                scenario = "Time is short, can just detect user is lying. ";
            }
        }

        return scenario;
    }

    public String getContextInfo() {
        return contextInfo;
    }

    public String getComplexADL() {
        return ComplexADL;
    }

    public String getActivityDuration() {
        return activityDuration + " minutes";
    }
}
