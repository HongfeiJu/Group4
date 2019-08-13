package com.example.group4;

import android.util.Log;

public class DataSmoother {

    public void smooth(float[] values, int window) {
        try {
            float sum;
            float[] data = new float[values.length];

            for (int i = 0; i < values.length; ++i) {
                sum = 0;
                for (int k = (i - window); k < (i + window); ++k) {

                    if ((0 > k) || (values.length < k)) {
                        continue;
                    } else {
                        sum = sum + values[k];
                    }
                }
                data[i] = sum / (window + window + 1);
            }

            for (int i = 0; i < values.length; ++i) {
                values[i] = data[i];
            }
        }
        catch (Exception e) {
            Log.e("Smooth", e.toString());
        }
    }
}
