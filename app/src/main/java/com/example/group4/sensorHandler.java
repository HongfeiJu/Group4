package com.example.group4;

/**
 * modified based on jlee375's version.
 * Credits: 1. https://stackoverflow.com/questions/27772011/how-to-export-data-to-csv-file-in-android
 *          2. https://github.com/techtribeyt/androidcsv/blob
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class sensorHandler extends Service implements SensorEventListener{

    public static final String BROADCAST_ACTION = "com.example.group4";

    private SensorManager accelManage; //sensor manager
    private Sensor senseAccel; //sensor object
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];
    int index = 0;
    int k=0;
    Bundle b;

    Handler handler;

    Intent sendingIntent;
    private FileWriter mFileWriter;


    @Override
    public void onCreate() {
        Log.i("info", "sensor service start");

        //creates accel sensor manager
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //accelerometer sensing
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor listener
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);


        handler = new Handler();
        sendingIntent = new Intent(BROADCAST_ACTION);

        handler.post(sendData);

        super.onCreate();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        //for accelerometer
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            //storing XY and Z values for accelerometer sensor
            accelValuesX[index] = sensorEvent.values[0];
            accelValuesY[index] = sensorEvent.values[1];
            accelValuesZ[index] = sensorEvent.values[2];

            Log.d("info", "generating data "+accelValuesX[index]+" "+accelValuesY[index]);
            if(index >= 127){
                index = 0;
                accelManage.unregisterListener(this);
                //callFallRecognition();
                accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);

            }
        }
    }

    public void exportData(View view){
        String filename = "sensor_log.csv";
        String entry = accelValuesX.toString() + "," +
                accelValuesY.toString() + "," +
                accelValuesZ.toString() + "\n";
        try {
            FileOutputStream out = openFileOutput( filename, Context.MODE_APPEND );
            out.write( entry.getBytes() );
            out.close();
            Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Runnable sendData = new Runnable() {
        @Override
        public void run() {
            Log.d("info", "generating data "+accelValuesX[index]+" "+accelValuesY[index]);
            sendingIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendingIntent.putExtra("acData", accelValuesX[index]+" "+accelValuesY[index]+" "+accelValuesZ[index]);
            sendBroadcast(sendingIntent);
            handler.postDelayed(sendData, 100);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
