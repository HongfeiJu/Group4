package com.example.group4;

import android.util.Log;


import java.util.ArrayList;

public class DataSmoother {

    public void smooth(ArrayList<Float> values, int window) {
        try {
            float sum;
            ArrayList<Float> data = new ArrayList<Float>(values.size());

            for (int i = 0; i < values.size(); ++i) {
                sum = 0;
                for (int k = (i - window); k <= (i + window); ++k) {

                    if ((0 > k) || (values.size()-1 < k)) {
                        continue;
                    } else {
                        sum = sum + values.get(k);
                    }
                }
                data.add(sum / (window + window + 1));
            }

            values.clear();
            values.addAll(data);
        }
        catch (Exception e) {
            Log.e("Smooth", e.toString());
        }
    }
}
