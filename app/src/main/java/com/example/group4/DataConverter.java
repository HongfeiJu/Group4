package com.example.group4;

import androidx.room.TypeConverter;

import org.json.JSONArray;

public class DataConverter {

    @TypeConverter
    public String fromArray(double[] data) {
        return data.toString();
    }

    @TypeConverter
    public double[] fromString(String j) {
        String[] d = j.split(",");
        double[] v = new double[d.length];
        for(int i = 0; i < d.length; ++i) {
            v[i] = Double.valueOf(d[i]);
        }
        return v;
    }
}
