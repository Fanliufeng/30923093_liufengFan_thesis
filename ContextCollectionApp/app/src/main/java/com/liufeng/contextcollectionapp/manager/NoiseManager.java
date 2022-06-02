package com.liufeng.contextcollectionapp.manager;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

/**
 * edited by liufeng Fan
 * this class is used to get noise level
 */
public class NoiseManager extends Service {
    private Context mContext;
    private static final String LOG_TAG = "AudioRecordTest";

    private MediaRecorder mRecorder =null;

    private static final long MIN_TIME_BW_UPDATES = 1000  * 1; // 1 minute

    public double noise_level=0;

    /** indicates whether or not Microphone Sensor is supported */
    public static Boolean supported;
    /** indicates whether or not Microphone Sensor is running */
    boolean running = false;

    public NoiseManager(Context context){
        this.mContext = context;
        startListening();

    }

    /**
     * Returns true if the manager is listening to orientation changes
     */
    public boolean isListening() {
        return running;
    }

    /**
     * Unregisters listeners
     */
    public void stopListening() {
        running = false;
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * Start detect noise level
     */
    public void getNoiseLevel(){
        try {
            if (isSupported(mContext)){
                startListening();
                // noise_level = getAmplitude();
                Log.e(LOG_TAG, "Start listening");
                Log.e(LOG_TAG, String.valueOf(noise_level));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns true if at least one microphone is available
     */
    public boolean isSupported(Context context) {
        mContext = context;
        if (supported == null) {
            if (mContext != null) {
                if (mContext.getPackageManager().hasSystemFeature( PackageManager.FEATURE_MICROPHONE)) {
                    if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.RECORD_AUDIO},0);
                    }
                    //Microphone is present on the device
                    return true;
                } else {
                    return false;
                }
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    /**
     * Registers a listener and start listening
     * @param
     *
     */
    public void startListening( )
    {
        if (mRecorder == null && isSupported(mContext)) {
            mRecorder = new MediaRecorder();
            //mRecorder.setAudioSamplingRate(44100);
            //mRecorder.setAudioEncodingBitRate(96000);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            //og.d("noise tester", String.valueOf(mRecorder.getMaxAmplitude()));

            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                Log.e(LOG_TAG, "prepare() failed");
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Log.e("noise tester2", String.valueOf(mRecorder.getMaxAmplitude()));

            running = true;
        }
    }

    /**
     * get max amplitude, first calling is zero
     * range is 0 - 32767
     * @return
     */
    public double getAmplitude() {
        if (mRecorder != null) {
            double max = mRecorder.getMaxAmplitude();
            return (max);
        }
        else {
            return 0;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
