package com.example.group4;

import android.util.Log;

import java.util.ArrayList;


enum Features {
    PPEAK, NPEAK, INTERCEPT, NONE;
}

public class FindFeatures {
    private float threshold = (float)0.0;

    public ArrayList<Features> tagFeatures(ArrayList<Float> values) {
        ArrayList<Features> features = new ArrayList<>();

        for(int i = 2; i < values.size()-1; ++i) {
            if(values.get(i-1) < values.get(i) && values.get(i + 1) < values.get(i)) {
                features.add(Features.PPEAK);
            } else if (values.get(i-1) > values.get(i) && values.get(i+1) > values.get(i)) {
                features.add(Features.NPEAK);
            } else if ((values.get(i-1) < 0) && (values.get(i) > 0)){
                features.add(Features.INTERCEPT);
            } else if ((values.get(i-1) > 0) && (values.get(i) < 0)) {
                features.add(Features.INTERCEPT);
            } else {
                continue;
            }
        }
        return features;
    }

    public ArrayList<ArrayList<Features>> tagAllFeatures(ArrayList<ArrayList<Float>> values) {
        ArrayList<ArrayList<Features>> features = new ArrayList<>();
        for(int i = 0; i < values.size(); ++i) {
            features.add(tagFeatures(values.get(i)));
        }
        return features;
    }
}
