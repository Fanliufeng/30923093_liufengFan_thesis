package com.liufeng.contextcollectionapp.fragment;

import static org.apache.commons.math3.util.Precision.round;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liufeng.contextcollectionapp.R;
import com.liufeng.contextcollectionapp.caarmodel.RecognitionModel;
import com.liufeng.contextcollectionapp.databinding.FragmentContextInfoBinding;
import com.liufeng.contextcollectionapp.databinding.FragmentUserSettingBinding;
import com.liufeng.contextcollectionapp.entity.Location;
import com.liufeng.contextcollectionapp.manager.GPSManager;
import com.liufeng.contextcollectionapp.manager.NetworkManager;
import com.liufeng.contextcollectionapp.manager.NoiseManager;
import com.liufeng.contextcollectionapp.manager.WiFiInfo;
import com.liufeng.contextcollectionapp.manager.WiFiManager;
import com.liufeng.contextcollectionapp.reader.ReadData;
import com.liufeng.contextcollectionapp.viewmodel.LocationViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserSettingFragment extends Fragment {

    public double longitude, latitude;
    private GPSManager gpsManager;
    private NetworkManager networkManager;
    private WiFiManager wiFiManager;
    public String SSID, BSSID;
    public ArrayList<String> locationList;
    public ArrayList<Double> latList, lonList;
    public ReadData readData;
    public RecognitionModel recognitionModel;

    private FragmentUserSettingBinding fragmentUserSettingBinding;
    private LocationViewModel locationViewModel;

    public UserSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentUserSettingBinding = FragmentUserSettingBinding.inflate(inflater,container,false);
        View view = fragmentUserSettingBinding.getRoot();



        locationViewModel =
                ViewModelProvider.AndroidViewModelFactory.
                        getInstance(getActivity().getApplication()).
                        create(LocationViewModel.class);

        getAllData();


        fragmentUserSettingBinding.mGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
                getNetworkStatus();
                //testReadData();
                getADL();
                getAverageNoiseLevel();
                checkIsHome();
                checkIsDayTime();
                checkIsDurationEnough();
                SetUserStatus();
            }
        });

        fragmentUserSettingBinding.mSetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fragmentUserSettingBinding.
                        nameTextField.getEditText().getText().toString().toLowerCase();
                if (!name.isEmpty() && latitude != 0 && longitude != 0){
                    if(!locationList.contains(name)){
                        if(!latList.contains(latitude) && !lonList.contains(longitude)){
                            Location currentLocation = new Location(name,latitude,longitude,SSID,BSSID);
                            locationViewModel.insert(currentLocation);
                            //getAllData();

                        }
                    }


                }
            }
        });

        fragmentUserSettingBinding.mUpdateCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationViewModel.deleteAll();
                getAllData();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    /**
     * Check is daytime
     */
    public void checkIsDayTime(){
        recognitionModel = new RecognitionModel(getActivity());
        try {
            boolean isDayTime = recognitionModel.checkDayTime();
            Log.e("Check day time: ", String.valueOf(isDayTime));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Check is duration
     */
    public void checkIsDurationEnough(){
        recognitionModel = new RecognitionModel(getActivity());
        try {
            boolean isDurationEnough = recognitionModel.checkDuration();
            Log.e("Check duration: ", String.valueOf(isDurationEnough));
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * Check is home
     */
    public void checkIsHome(){
        recognitionModel = new RecognitionModel(getActivity());
        try {
            boolean isHome = recognitionModel.checkInHome();
            Log.e("Activity at Home: ", String.valueOf(isHome));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * change unit of speed, from m/s to km/h
     */
    private double roundLocation(double newLocation){
        double roundLocation = round(newLocation,3, BigDecimal.ROUND_HALF_UP);
        return roundLocation;
        //return newSpeed*3.6;
    }

    /**
     * Get ADL
     */
    public void getADL(){
        recognitionModel = new RecognitionModel(getActivity());
        try {
            String ADL = recognitionModel.getSimpleARResult();
            Log.e("Recognized Activity: ", ADL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get Average noise level
     */
    public void getAverageNoiseLevel(){
        recognitionModel = new RecognitionModel(getActivity());
        try {
            double avgNoiseLevel = recognitionModel.getAverageNoiseLevel();
            Log.e("Average Noise Level: ", String.valueOf(avgNoiseLevel));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * View all data
     */
    public void getAllData(){
        locationViewModel.getAllLocations().observe(getViewLifecycleOwner(), new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable final List<Location> locations) {
                String allLocationInfo = "";
                // Initialize location list
                locationList = new ArrayList<>();
                latList = new ArrayList<>();
                lonList = new ArrayList<>();
                for (Location temp : locations ){
                    locationList.add(temp.loc_name);
                    latList.add(temp.loc_lat);
                    lonList.add(temp.loc_lon);

                    String locationDetails = ("Location id: " + temp.uid + ", "
                            + "Name: " + temp.loc_name + ", Lat: "  + temp.loc_lat
                            + ", Lon: " + temp.loc_lon + ", WiFi name: " + temp.loc_ssid +
                            ", BSSID: " + temp.loc_bssid);
                    allLocationInfo = allLocationInfo +
                            System.getProperty("line.separator")+locationDetails;
                }
                fragmentUserSettingBinding.textViewRead.setText("All data" + allLocationInfo);


            }
        });
    }

    /**
     * Get location using gps
     * Lat, Long, and speed
     */
    public void getCurrentLocation(){
        gpsManager = new GPSManager(getActivity());
        if(gpsManager.canGetLocation()){
            latitude = roundLocation(gpsManager.getLatitude());
            longitude = roundLocation(gpsManager.getLongitude());

            fragmentUserSettingBinding.textCurrentLat.setText(String.valueOf(latitude));
            fragmentUserSettingBinding.textCurrentLon.setText(String.valueOf(longitude));
        }
        else {
            gpsManager.showSettingsAlert();
        }
    }

    /**
     * Get network status
     */
    public void getNetworkStatus(){
        networkManager = new NetworkManager(getActivity());
        if (networkManager.getNetworkStatus()){
            if (networkManager.getWifiStatus()){
                getWifiInfo();
            }
            if (networkManager.getMobileStatus()){
                SSID = "None";
                BSSID = "None";
                fragmentUserSettingBinding.textCurrentWifiSSID.setText(SSID);
                fragmentUserSettingBinding.textCurrentBSSID.setText(BSSID);
            }
        }
        else{
            SSID = "None";
            BSSID = "None";
            fragmentUserSettingBinding.textCurrentWifiSSID.setText(SSID);
            fragmentUserSettingBinding.textCurrentBSSID.setText(BSSID);

        }
    }
    /**
     * Get user status
     */
    public void SetUserStatus(){
        recognitionModel = new RecognitionModel(getActivity());
        fragmentUserSettingBinding.textViewUserStatus.setText(recognitionModel.getScenario());
    }

    /**
     * Get wifi information
     * name, bssid, rssi
     */
    public void getWifiInfo(){
        wiFiManager = new WiFiManager(getActivity());
        WiFiInfo newWifiInfo = wiFiManager.getNewWifiInfo();

        SSID = newWifiInfo.getSsid();
        BSSID = newWifiInfo.getBssid();

        SSID = SSID.replaceAll("\"","");

        fragmentUserSettingBinding.textCurrentWifiSSID.setText(SSID);
        fragmentUserSettingBinding.textCurrentBSSID.setText(BSSID);
    }

    public void testReadData(){
        readData = new ReadData(getActivity());
        readData.readNoiseData();
        double sumNoise = 0;
        ArrayList<Double> noiseLevelList = readData.getNoiseLevelList();

        for(Double temp:noiseLevelList){
            sumNoise += temp;
        }
        double average_noise = sumNoise/readData.getNoiseLevelList().size();
        Log.e("Average nosie Level", String.valueOf(average_noise));
    }
}