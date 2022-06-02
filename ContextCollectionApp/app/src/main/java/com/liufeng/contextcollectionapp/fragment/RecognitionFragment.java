package com.liufeng.contextcollectionapp.fragment;

import static java.lang.Math.round;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liufeng.contextcollectionapp.R;
import com.liufeng.contextcollectionapp.caarmodel.RecognitionModel;
import com.liufeng.contextcollectionapp.databinding.FragmentRecognitionBinding;
import com.liufeng.contextcollectionapp.manager.BTManager;

public class RecognitionFragment extends Fragment {

    private FragmentRecognitionBinding fragmentRecognitionBinding;
    public RecognitionModel recognitionModel;
    public BTManager BTManager;
    public Drawable sleepingImage, workingImage, carImage, busImage, homeImage, outdoorImage ;
    public Drawable dayImage, nightImage, BTonImage, BToffImage;
    public Drawable lyingImage, standingImage, sittingImage, walkingImage, upstairsImage, downstairsImage;
    public RecognitionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecognitionBinding = FragmentRecognitionBinding.inflate(inflater, container, false);
        View view = fragmentRecognitionBinding.getRoot();

        sleepingImage = getResources().getDrawable(R.drawable.sleep_icon_2);
        workingImage = getResources().getDrawable(R.drawable.working_icon);
        carImage = getResources().getDrawable(R.drawable.car_icon2);
        busImage = getResources().getDrawable(R.drawable.bus_icon);
        outdoorImage = getResources().getDrawable(R.drawable.outdoor_icon);
        dayImage = getResources().getDrawable(R.drawable.morning_icon);
        nightImage = getResources().getDrawable(R.drawable.night_icon2);
        BTonImage = getResources().getDrawable(R.drawable.bton_image);
        BToffImage = getResources().getDrawable(R.drawable.btoff_image);
        lyingImage = getResources().getDrawable(R.drawable.lying_icon);
        standingImage =getResources().getDrawable(R.drawable.standing_icon);
        sittingImage = getResources().getDrawable(R.drawable.sitting_icon);
        downstairsImage = getResources().getDrawable(R.drawable.downstairs_icon);
        upstairsImage = getResources().getDrawable(R.drawable.upstairs_icon);
        walkingImage = getResources().getDrawable(R.drawable.activity_icon);


        homeImage = getResources().getDrawable(R.drawable.home_icon_2);

        SetUserStatus();
        checkBTStatus();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    /**
     * Get user status
     */
    public void SetUserStatus(){
        recognitionModel = new RecognitionModel(getActivity());
        if(recognitionModel.checkInHome()){
            fragmentRecognitionBinding.locationImage.setImageDrawable(homeImage);
            fragmentRecognitionBinding.textLocation.setText("Home");
        }else{
            fragmentRecognitionBinding.locationImage.setImageDrawable(outdoorImage);
            fragmentRecognitionBinding.textLocation.setText("Outdoor");
        }

        if(recognitionModel.checkDayTime()){
            fragmentRecognitionBinding.textTime.setText("Daytime");
            fragmentRecognitionBinding.timeImage.setImageDrawable(dayImage);
        }else{
            fragmentRecognitionBinding.textTime.setText("Nighttime");
            fragmentRecognitionBinding.timeImage.setImageDrawable(nightImage);
        }

        String scenario = recognitionModel.getAllScenario();
        String complexADL = recognitionModel.getComplexADL();
        String contextInfo = recognitionModel.getContextInfo();
        setActivityImage(scenario);
        fragmentRecognitionBinding.textADL.setText(scenario);
        fragmentRecognitionBinding.textADLDetail.setText(complexADL);
        //fragmentRecognitionBinding.textCAHARProcess.setText(contextInfo);

        String speed = recognitionModel.getAverageSpeed() + " KM/H";
        fragmentRecognitionBinding.textAvgSpeed.setText(speed);

        String noiseLevel = recognitionModel.getAverageNoiseLevel() + " DB";
        fragmentRecognitionBinding.textAvgSound.setText(noiseLevel);

        if(recognitionModel.checkScreenOn()){
            fragmentRecognitionBinding.textScreenStatus.setText("On");
        }else {
            fragmentRecognitionBinding.textScreenStatus.setText("Off");
        }
        recognitionModel.checkDuration();
        String duration = recognitionModel.activityDuration+ " Mins";
        double durationD = Double.parseDouble(recognitionModel.activityDuration);
        if (durationD >= 60){
            duration = durationD/60.0 + " Hours";
        }


        fragmentRecognitionBinding.textDuration.setText(duration);

        if(recognitionModel.getBTStatus().equalsIgnoreCase("Laptop on")){
            fragmentRecognitionBinding.BTStatusImage.setImageDrawable(BTonImage);
            fragmentRecognitionBinding.textBtStatus.setText("Laptop On");
        }else if(recognitionModel.getBTStatus().equalsIgnoreCase("Carplay on")){
            fragmentRecognitionBinding.BTStatusImage.setImageDrawable(BTonImage);
            fragmentRecognitionBinding.textBtStatus.setText("Carplay On");
        }else {
            fragmentRecognitionBinding.BTStatusImage.setImageDrawable(BToffImage);
            fragmentRecognitionBinding.textBtStatus.setText("None");
        }

        String simpleHar = recognitionModel.getSimpleARResult();
        setSimpleHARImage(simpleHar);


    }

    /**
     * check bluetooth status
     */
    public void checkBTStatus(){
        BTManager = new BTManager(getActivity());
        boolean status = BTManager.isConnectClassicBT("04:52:C7:25:44:1D");
        Log.e("BT status: ", String.valueOf(status));


        // 57:B5:7D:7E:FC:2F

    }

    public void setActivityImage(String activity){
        if(activity.equalsIgnoreCase("Commuting by public transport")){
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(busImage);
        }else if(activity.equalsIgnoreCase("Commuting by private vehicle")){
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(carImage);
        }else if(activity.equalsIgnoreCase("Studying (Home Activities)")){
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(workingImage);
        }else if(activity.equalsIgnoreCase("Home activities (Sleeping)")){
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(sleepingImage);
        }else if(activity.equalsIgnoreCase("Home activities")){
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(homeImage);
        }else {
            fragmentRecognitionBinding.dailyActivityImage.setImageDrawable(outdoorImage);
        }
    }

    public void setSimpleHARImage(String activity){
        if(activity.equalsIgnoreCase("LYING")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(lyingImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }else if(activity.equalsIgnoreCase("STANDING")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(standingImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }else if(activity.equalsIgnoreCase("WALKING")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(walkingImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }else if(activity.equalsIgnoreCase("WALKING_UPSTAIRS")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(upstairsImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }else if(activity.equalsIgnoreCase("SITTING")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(sittingImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }else if(activity.equalsIgnoreCase("WALKING_DOWNSTAIRS")){
            fragmentRecognitionBinding.activityImage.setImageDrawable(downstairsImage);
            fragmentRecognitionBinding.textActivityLabel.setText(activity);
        }
    }



}