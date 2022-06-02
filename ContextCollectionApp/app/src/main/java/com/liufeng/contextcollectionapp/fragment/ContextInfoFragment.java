package com.liufeng.contextcollectionapp.fragment;

import static org.apache.commons.math3.util.Precision.round;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liufeng.contextcollectionapp.R;
import com.liufeng.contextcollectionapp.databinding.FragmentContextInfoBinding;
import com.liufeng.contextcollectionapp.entity.Location;
import com.liufeng.contextcollectionapp.manager.BTManager;
import com.liufeng.contextcollectionapp.manager.BatteryInfoManager;
import com.liufeng.contextcollectionapp.manager.GPSManager;
import com.liufeng.contextcollectionapp.manager.MotionSensorManager;
import com.liufeng.contextcollectionapp.manager.NetworkManager;
import com.liufeng.contextcollectionapp.manager.NoiseManager;
import com.liufeng.contextcollectionapp.manager.ScreenManager;
import com.liufeng.contextcollectionapp.manager.WiFiInfo;
import com.liufeng.contextcollectionapp.manager.WiFiManager;
import com.liufeng.contextcollectionapp.manager.calendar.CalenderDataStruct;
import com.liufeng.contextcollectionapp.manager.calendar.calendarUtil;
import com.liufeng.contextcollectionapp.viewmodel.LocationViewModel;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This fragment is used to collect context information
 */
public class ContextInfoFragment extends Fragment {
    private float battery_level;
    private boolean isCharging;
    private LocationViewModel locationViewModel;

    private CSVWriter writer = null;
    private CSVWriter writer2 = null;
    public ArrayList<String> locationList, lat_lon_list, lat_BSSID_List;


    public BTManager BTManager;
    MediaRecorder recorder;
    public boolean networkConnected, wifiConnected,  mobileConnected;
    private GPSManager gpsManager;
    private NetworkManager networkManager;
    private NoiseManager noiseManager;
    private WiFiManager wiFiManager;
    private BatteryInfoManager batteryInfoManager;
    private MotionSensorManager motionSensorManager;
    private ScreenManager screenManager;
    public String SSID, BSSID, RSSI;
    public double longitude, latitude, speed;
    public double amplitudeDb;
    public String csv_file, csv_file2;
    public Timer timer, timer2;
    public TimerTask timerTask, timerTask2;
    public String netStatus, batteryStatus, screenStatus, BTStatus;
    public boolean isScreenOn;
    public String calenderStatusText, eventLocation;
    private StringBuilder EventInfo;
    private  static  final String TAG = "CalendarActivity";
    public String locationName = "none";

    private FragmentContextInfoBinding fragmentContextInfoBinding;
    public ContextInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       fragmentContextInfoBinding = FragmentContextInfoBinding.inflate(inflater,container,false);
       View view = fragmentContextInfoBinding.getRoot();

       // Initialize viewModel
       locationViewModel =
                ViewModelProvider.AndroidViewModelFactory.
                        getInstance(getActivity().getApplication()).
                        create(LocationViewModel.class);
        getAllData();



        motionSensorManager = new MotionSensorManager(getActivity());
        noiseManager = new NoiseManager(getActivity());
        screenManager = new ScreenManager(getActivity());

        // Set csv files name
        csv_file = (getActivity().getFilesDir() + "/" + "noise_raw_data_commuting2.csv");
        csv_file2 = getActivity().getFilesDir() + "/" + "other_context_data_commuting2.csv";

        // First initialize context information
        getLocation();
        getNetworkStatus();
        getBatteryStatus();

        fragmentContextInfoBinding.mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                getSimpleActivity();
                try {
                    // Clear csv file when start
                    fragmentContextInfoBinding.txtRealTime.setBase(SystemClock.elapsedRealtime());
                    fragmentContextInfoBinding.txtRealTime.start();
                    FileWriter fw = new FileWriter(csv_file,false);
                    FileWriter fw2 = new FileWriter(csv_file2,false);
                    fw.close();
                    fw2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StartCollectNoiseLevel();
                startCollectOtherContext();
            }
        });

        fragmentContextInfoBinding.mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPrediction();
                try {
                    fragmentContextInfoBinding.txtRealTime.stop();
                    writer.close();
                    writer2.close();
                    timer.cancel();
                    timerTask.cancel();
                    timer2.cancel();
                    timerTask2.cancel();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    /**
     * Check bluetooth status
     */
    public void checkBTStatus(){
        BTManager = new BTManager(getActivity());
        BTStatus = "None";
        //04:52:C7:25:44:1D
        if (BTManager.isConnectClassicBT("57:B5:7D:7E:FC:2F")){
            BTStatus = "Laptop on";


        }else if(BTManager.isConnectClassicBT("04:52:C7:25:44:1D")){
            BTStatus = "Carplay on";
        }
        fragmentContextInfoBinding.textBTStatus.setText(BTStatus);
    }

    /**
     * View all data
     */
    public void getAllData(){
        locationList = new ArrayList<>();
        lat_lon_list = new ArrayList<>();
        lat_BSSID_List = new ArrayList<>();
        locationViewModel.getAllLocations().observe(getViewLifecycleOwner(), new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable final List<Location> locations) {

                // Initialize location list
                //locationList = new ArrayList<>();
                //lat_lon_list = new ArrayList<>();
                for (Location temp : locations ){
                    String lat_lon = temp.loc_lat +
                            "-" + temp.loc_lon;
                    locationList.add(temp.loc_name);
                    lat_lon_list.add(lat_lon);
                    lat_BSSID_List.add(temp.loc_bssid);
                }
            }
        });
    }

    /**
     * Get CalenderInfo
     */
    public void getCalendarInfo(){
        ArrayList<CalenderDataStruct> calenderDataStructs = calendarUtil.GetCurrentSchedule(getActivity());
        Log.i(TAG, "onCreate: calenderDataStructs "+ calenderDataStructs.size());

        if (calenderDataStructs.size() == 0){
            calenderStatusText = "No Event";
            fragmentContextInfoBinding.calendarStatusText.setText(calenderStatusText);
            eventLocation = "None";

        }else {
            calenderStatusText = "";
            for (CalenderDataStruct item : calenderDataStructs){
                eventLocation = item.getLocation();
                calenderStatusText = calenderStatusText + item.toString();
                Log.i(TAG, "onCreate: CalenderDataStruct "+ item.toString());
            }
            fragmentContextInfoBinding.calendarStatusText.setText(calenderStatusText);

        }

    }

    /**
     * Get charging status
     */
    public void getBatteryStatus(){

        batteryInfoManager = new BatteryInfoManager(getActivity());
        if(batteryInfoManager.isBatteryCharging()){
            //mBatteryStatus.setText("Charging");
            batteryStatus = "Charging";
            fragmentContextInfoBinding.textBatteryStatus.setText(batteryStatus);
        }
        else {
            //mBatteryStatus.setText("Not Charging");
            batteryStatus = "Not Charging";
            fragmentContextInfoBinding.textBatteryStatus.setText(batteryStatus);
        }
    }

    /**
     * Get network status
     */
    public void getNetworkStatus(){
        networkManager = new NetworkManager(getActivity());
        if (networkManager.getNetworkStatus()){
            if (networkManager.getWifiStatus()){
                //mNetworkStatus.setText("WiFi Connected");
                fragmentContextInfoBinding.textNetworkStatus.setText("WiFi Connected");
                netStatus = "WiFi";
                getWifiInfo();
            }
            if (networkManager.getMobileStatus()){

                //mNetworkStatus.setText("Mobile cellular Connected");
                fragmentContextInfoBinding.textNetworkStatus.setText("Mobile cellular Connected");
                netStatus = "Cellular";
                SSID = "None";
                BSSID = "None";
                RSSI = "None";
                fragmentContextInfoBinding.textSSID.setText(SSID);
                fragmentContextInfoBinding.textBSSID.setText(BSSID);
                fragmentContextInfoBinding.textRSSI.setText(RSSI);

                //mSSID.setText("None");
                //mBSSID.setText("None");
                //mRssi.setText("None");
            }
        }
        else{
            //mNetworkStatus.setText("Not network connected");
            fragmentContextInfoBinding.textNetworkStatus.setText("Not network connected");
            netStatus = "None";
            SSID = "None";
            BSSID = "None";
            RSSI = "None";

            fragmentContextInfoBinding.textSSID.setText(SSID);
            fragmentContextInfoBinding.textBSSID.setText(BSSID);
            fragmentContextInfoBinding.textRSSI.setText(RSSI);

            //mSSID.setText("None");
            //mBSSID.setText("None");
            //mRssi.setText("None");
        }
    }

    /**
     * Get location using gps
     * Lat, Long, and speed
     */
    public void getLocation(){
        gpsManager = new GPSManager(getActivity());

        if(gpsManager.canGetLocation()){
            latitude = roundLocation(gpsManager.getLatitude());
            longitude = roundLocation(gpsManager.getLongitude());


            speed = gpsManager.getSpeed();
            double speed_km_h = formatSpeed(speed);

            fragmentContextInfoBinding.textLat.setText(String.valueOf(latitude));
            fragmentContextInfoBinding.textLon.setText(String.valueOf(longitude));
            fragmentContextInfoBinding.textSpeed.setText(String.valueOf(speed_km_h) + "KM/H");

            //mLat.setText(String.valueOf(latitude));
            //mLong.setText(String.valueOf(longitude));
            //mSpeed.setText(String.valueOf(speed_km_h) + "KM/H");
        }
        else {
            gpsManager.showSettingsAlert();
        }
    }

    /**
     * Get Location name
     */
    public void getLocationName(){

        String lat_lon = latitude+ "-" + longitude;

        if(lat_lon_list.size() != 0 && lat_lon_list.contains(lat_lon)){
            int index = lat_lon_list.indexOf(lat_lon);
            locationName = locationList.get(index);
            Log.e("Check location name", "Location is on list");

        }
        else {
            locationName = "Unknown";
            Log.e("Check location name", "Location is not on list");
        }

        if(lat_lon_list.size() != 0 && lat_BSSID_List.contains(BSSID)){
            int index = lat_lon_list.indexOf(lat_lon);
            locationName = locationList.get(index);
            Log.e("Check location WiFi", "Location is not on list");
        }

        fragmentContextInfoBinding.textLocationName.setText(locationName);
        Log.e("location name", locationName);

    }

    /**
     * Start recognize activity
     */
    public void getSimpleActivity(){
        motionSensorManager.startPrediction();
    }

    /**
     * Stop recognize activity
     */
    public void stopPrediction(){
        motionSensorManager.stopSensors();
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
        RSSI = String.valueOf(newWifiInfo.getRssi());
        SSID = SSID.replace("\"", "");

        fragmentContextInfoBinding.textSSID.setText(SSID);
        fragmentContextInfoBinding.textBSSID.setText(BSSID);
        fragmentContextInfoBinding.textRSSI.setText(RSSI);

        //mSSID.setText(SSID);
        //mBSSID.setText(BSSID);
        //mRssi.setText(RSSI);
    }

    /**
     * get screen status
     * @return String screenStatus
     */
    public String getScreenStatus(){
        if(screenManager.getScreenStatus()){
            screenStatus = "Off";
            //mScreenStatus.setText(screenStatus);
            fragmentContextInfoBinding.textScreenStatus.setText(screenStatus);
        }else {
            screenStatus = "On";
            //mScreenStatus.setText(screenStatus);
            fragmentContextInfoBinding.textScreenStatus.setText(screenStatus);
        }
        return screenStatus;

    }

    /**
     * Start periodic work to get context information
     */
    public void startCollectOtherContext(){
        try {
            writer2 = new CSVWriter(new FileWriter(csv_file2,true));
            timer2 = new Timer();
            timerTask2 = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLocation();
                            getNetworkStatus();
                            getBatteryStatus();
                            //getScreenStatus();
                            getCalendarInfo();
                            getLocationName();
                            checkBTStatus();


                            Log.e("Screen status", String.valueOf(screenManager.getScreenStatus()));

                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String [] row = new String[14];
                            // Timestamp
                            row[0] = "" +timestamp.getTime();
                            // Latitude
                            row[1] = String.valueOf(latitude);
                            // Longitude
                            row[2] = String.valueOf(longitude);
                            // Speed
                            row[3] = String.valueOf(formatSpeed(speed));
                            // Network Status
                            row[4] = netStatus;
                            // Name of wifi
                            row[5] = SSID;
                            // SSID
                            row[6] = BSSID;
                            // wifi strength
                            row[7] = RSSI;
                            // battery status
                            row[8] = batteryStatus;
                            // screen status
                            row[9] = getScreenStatus();

                            // location name
                            row[10] = locationName;
                            row[11] = calenderStatusText;
                            row[12] = eventLocation;
                            row[13] = BTStatus;
                            writer2.writeNext(row);
                        }
                    });
                }
            };
            timer2.scheduleAtFixedRate(timerTask2, 0, 5000);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Start periodic work to get noise information
     */
    public void StartCollectNoiseLevel(){

        try {
            writer = new CSVWriter(new FileWriter(csv_file,true));
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            testNoise();
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String [] row = new String[2];
                            if(!Double.isInfinite(amplitudeDb)){
                                row[0] = "" +timestamp.getTime();
                                row[1] = String.valueOf(amplitudeDb);
                                writer.writeNext(row);
                            }
                            else {
                                Log.e("Noise detection error",String.valueOf(amplitudeDb));
                            }
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 500);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * change unit of speed, from m/s to km/h
     */
    private double formatSpeed(double newSpeed){
        double currentSpeed = round(newSpeed,3, BigDecimal.ROUND_HALF_UP);
        double kmphSpeed = round((currentSpeed*3.6),3,BigDecimal.ROUND_HALF_UP);
        return kmphSpeed;
        //return newSpeed*3.6;
    }

    /**
     * Round number to 4 decimal
     */
    private double roundLocation(double newLocation){
        double roundLocation = round(newLocation,3, BigDecimal.ROUND_HALF_UP);
        return roundLocation;

    }

    /**
     * Get noise level
     */
    public void testNoise(){
        double DB = noiseManager.getAmplitude();

        amplitudeDb = Math.abs(20 * Math.log10((double)Math.abs(DB)));
        fragmentContextInfoBinding.textNoiseLevel.setText(String.valueOf(amplitudeDb));
        //mNoiseLevel.setText(String.valueOf(amplitudeDb));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}