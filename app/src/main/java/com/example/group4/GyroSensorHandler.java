package com.example.group4;

/**
 * modified based on jlee375's version.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class GyroSensorHandler extends Service implements SensorEventListener{

    public static final String BROADCAST_ACTION = "com.example.group4";

    private SensorManager manager;
    private Sensor senseGyro;
    float gyroX[] = new float[128];
    float gryoY[] = new float[128];
    float gryoZ[] = new float[128];
    int index = 0;

    Handler handler;

    Intent sendingIntent;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        //Log.i("info", "sensor type: " + mySensor.getName());

        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            index++;
            gyroX[index] = sensorEvent.values[0];
            gryoY[index] = sensorEvent.values[1];
            gryoZ[index] = sensorEvent.values[2];
            Log.d("info", "gyro data "+gyroX[index]+" "+gryoY[index]+" "+gryoZ[index]);
            if(index >= 127){
                index = 0;
                manager.unregisterListener(this);
                //callFallRecognition();
                manager.registerListener(this, senseGyro, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onCreate(){
        Log.i("info", "gyro sensor service start");

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        senseGyro = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        manager.registerListener(this, senseGyro, SensorManager.SENSOR_DELAY_GAME);

        handler = new Handler();
        sendingIntent = new Intent(BROADCAST_ACTION);

        handler.post(sendData);

    }

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
            //Log.d("info", "generating data "+accelValuesX[index]+" "+accelValuesY[index]);
            sendingIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendingIntent.putExtra("gyroData", gyroX[index]+" "+gryoY[index]+" "+gryoZ[index]);
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
