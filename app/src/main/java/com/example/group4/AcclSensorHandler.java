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
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class AcclSensorHandler extends Service implements SensorEventListener{

    public static final String BROADCAST_ACTION = "com.example.group4";

    private SensorManager manager;
    private Sensor senseAccel;
    float[] accelValuesX = new float[128];
    float[] accelValuesY = new float[128];
    float[] accelValuesZ = new float[128];
    int index = 0;

    Handler handler;

    Intent sendingIntent;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            accelValuesX[index] = sensorEvent.values[0];
            accelValuesY[index] = sensorEvent.values[1];
            accelValuesZ[index] = sensorEvent.values[2];
            //Log.d("info", "acce data "+accelValuesX[index]+" "+accelValuesY[index]);
            if(index >= 127){
                index = 0;
                manager.unregisterListener(this);
                //callFallRecognition();
                manager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onCreate(){
        Log.i("info", "sensor service start");

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_GAME);

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
