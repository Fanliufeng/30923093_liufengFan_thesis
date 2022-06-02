package com.liufeng.contextcollectionapp.manager;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.liufeng.contextcollectionapp.classifier.Classifier;
import com.liufeng.contextcollectionapp.classifier.TensorFlowHARClassifier;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * edited by liufeng Fan
 * this class is used to get motion sensor data and recognize activity
 */

public class MotionSensorManager implements SensorEventListener {
    private static final String EXTRA_TEXT = "text";

    private Context mContext;
    private String predictedActivity;

    private static final String MODEL_PATH = "rnn_model_9_features.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels2.txt";
    private static final int N_SAMPLES = 128;
    private String csv_file,csv_file2,csv_file3;
    private CSVWriter writer;
    private CSVWriter writer2,writer3;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();


    private List<String> resultList;

    // max position
    int idx = -1;

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    //private static List<Float> mp;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;

    //private Sensor mPressure;

    // only true then collect data from sensors
    private boolean mIsSensorUpdateEnabled = false;
    private boolean isListening = false;

    private float[][] results;
    private String[] labels = {"LYING","WALKING", "WALKING_UPSTAIRS", "WALKING_DOWNSTAIRS", "SITTING", "STANDING"};

    public MotionSensorManager(Context context){
        this.mContext = context;
    }

    public void startPrediction(){
        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        writer = null;
        writer2 = null;
        try {
            csv_file = (mContext.getFilesDir() + "/" + "raw_data_commuting2.csv");
            csv_file2 = (mContext.getFilesDir() +"/" + "Activity_Predict_commuting2.csv");
            FileWriter fw = new FileWriter(csv_file,false);
            FileWriter fw2 = new FileWriter(csv_file2,false);
            fw.close();
            fw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isListening = true;
        resultList = new ArrayList<>();
        initTensorFlowAndLoadModel();
        startSensors();

    }

    /**
     * initialize classifier
     */
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowHARClassifier.create(
                            mContext.getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            N_SAMPLES,
                            QUANT);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    /**
     * start sensors, including linear accelerometer, accelerometer, and gyroscope
     * sample rate is 50 ms
     */
    private void startSensors(){
        if (mAccelerometer!=null && mLinearAcceleration!=null && mGyroscope!=null){
            mSensorManager.registerListener(this, mAccelerometer, 20);
            mSensorManager.registerListener(this, mLinearAcceleration, 20);
            mSensorManager.registerListener(this, mGyroscope, 20);
            mIsSensorUpdateEnabled =true;
        }else{
            Log.e("motionManager", "start sensor");
            //ToastUtil.showText((Activity) mContext, "Not all required sensor is detected. The result may inaccurate.");
        }
    }

    /**
     * Stop all motion sensors
     */
    public void stopSensors(){
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mLinearAcceleration);
        mSensorManager.unregisterListener(this, mGyroscope);
        mIsSensorUpdateEnabled =false;
        isListening = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!mIsSensorUpdateEnabled) {
            stopSensors();
            Log.e("Sensor", "Sensor Update disabled. returning");
            return;
        }

        activityPrediction();

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);


        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);


        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Start activity recognition
     * @return String recognized activity
     */
    private String activityPrediction() {

        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES
                && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
                && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
        ) {
            if (isListening)
            {
                float[][][] data = new float[1][128][9];
                try {
                    csv_file = (mContext.getFilesDir() +"/" + "raw_data_commuting2.csv");
                    writer = new CSVWriter(new FileWriter(csv_file,true));

                    for (int i = 0; i < 128; i++) {

                        data[0][i][0] = gx.get(i);
                        data[0][i][1] = gy.get(i);
                        data[0][i][2] = gz.get(i);
                        data[0][i][3] = lx.get(i);
                        data[0][i][4] = ly.get(i);
                        data[0][i][5] = lz.get(i);
                        data[0][i][6] = ax.get(i);
                        data[0][i][7] = ay.get(i);
                        data[0][i][8] = az.get(i);


                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                        String [] row = new String[10];
                        row[0] = "" +timestamp.getTime();
                        row[1] = ""+gx.get(i);
                        row[2] = ""+gy.get(i);
                        row[3] = ""+gz.get(i);
                        row[4] = ""+lx.get(i);
                        row[5] = ""+ly.get(i);
                        row[6] = ""+lz.get(i);
                        row[7] = ""+ax.get(i);
                        row[8] = ""+ay.get(i);
                        row[9] = ""+az.get(i);
                        writer.writeNext(row);
                    }
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                results = classifier.recognizeHAR(data);
                idx = -1;
                float max = -1;
                for (int i = 0; i < results[0].length; i++) {
                    if (results[0][i] > max) {
                        idx = i;
                        max = results[0][i];
                    }
                }
                resultList.add(labels[idx]);

                predictedActivity = labels[idx];
                try {
                    csv_file2 = (mContext.getFilesDir() +"/" + "Activity_Predict_commuting2.csv");
                    writer2 = new CSVWriter(new FileWriter(csv_file2,true));

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    String [] row = new String[2];
                    row[0] = "" +timestamp.getTime();
                    row[1] = labels[idx];
                    writer2.writeNext(row);
                    writer2.close();

                }catch (IOException e) {
                    e.printStackTrace();
                }
                ax.clear(); ay.clear(); az.clear();
                lx.clear(); ly.clear(); lz.clear();
                gx.clear(); gy.clear(); gz.clear();

            }else{
                stopSensors();
            }
        }
        return predictedActivity;
    }

}
