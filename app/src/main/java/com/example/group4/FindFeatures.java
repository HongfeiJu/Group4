package com.example.group4;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

enum Features {
    PPEAK, NPEAK, INTERCEPT;
}

public class FindFeatures {
    private float threshold = (float)0.5;

    public ArrayList<Features> tagFeatures(ArrayList<Float> values) {
        ArrayList<Features> features = new ArrayList<>();

        for(int i = 2; i < values.size()-1; ++i) {
            if(((values.get(i - 1) - values.get(i)) < (-1*threshold)) &&
                    ((values.get(i) - values.get(i + 1)) > threshold)) {
                features.add(Features.PPEAK);
            } else if (((values.get(i - 1) - values.get(i)) > threshold) &&
                    ((values.get(i) - values.get(i + 1)) < (-1*threshold))) {
                features.add(Features.NPEAK);
            } else if (((values.get(i-1) < 0) && (values.get(i) > 0)) ||
                    ((values.get(i-1) > 0) && (values.get(i) < 0))) {
                features.add(Features.INTERCEPT);
            } else {
                continue;
            }
        }
        Log.i("Features", features.toString());
        return features;
    }
}
