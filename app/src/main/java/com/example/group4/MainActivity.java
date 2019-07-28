package com.example.group4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("info", "main activity start");
        Intent startSenseService = new Intent(MainActivity.this, sensorHandler.class);
        Bundle b = new Bundle();
        b.putString("phone", "1234");
        startSenseService.putExtras(b);
        startService(startSenseService);
    }

}
