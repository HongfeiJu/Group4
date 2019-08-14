package com.example.group4;

import android.util.Log;
import android.widget.HeaderViewListAdapter;

import java.util.ArrayList;

enum Gestures {
    COP, HEAD, HUNGRY, ABOUT, NONE
}

public class Classifier {

    private Features[][] copFeatures = {{Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK}, 
            {Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}};
    
    private Features[][] headFeatures = {{Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}};


    private Features[][] hungryFeatures = {{Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}, 
            {Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}};

    private Features[][] aboutFeatures = {{Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK},
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK}, 
            {Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.INTERCEPT, Features.NPEAK}, 
            {Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK}, 
            {Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.PPEAK, Features.INTERCEPT, Features.NPEAK, Features.PPEAK, Features.NPEAK, Features.INTERCEPT, Features.INTERCEPT, Features.NPEAK, Features.INTERCEPT, Features.PPEAK}};


    private ArrayList<ArrayList<Features>> copFeaturesList;
    private ArrayList<ArrayList<Features>> headFeaturesList;
    private ArrayList<ArrayList<Features>> hungryFeaturesList;
    private ArrayList<ArrayList<Features>> aboutFeaturesList;


    public Classifier() {
        copFeaturesList = new ArrayList<>();
        headFeaturesList = new ArrayList<>();
        hungryFeaturesList = new ArrayList<>();
        aboutFeaturesList = new ArrayList<>();


        for(int i = 0; i < copFeatures.length; ++i) {
            copFeaturesList.add(new ArrayList<Features>());
            for(int k = 0; k < copFeatures[i].length; ++k) {
                copFeaturesList.get(i).add(copFeatures[i][k]);
            }
        }

        for(int i = 0; i < headFeatures.length; ++i) {
            headFeaturesList.add(new ArrayList<Features>());
            for(int k = 0; k < headFeatures[i].length; ++k) {
                headFeaturesList.get(i).add(headFeatures[i][k]);
            }
        }

        for(int i = 0; i < hungryFeatures.length; ++i) {
            hungryFeaturesList.add(new ArrayList<Features>());
            for(int k = 0; k < hungryFeatures[i].length; ++k) {
                hungryFeaturesList.get(i).add(hungryFeatures[i][k]);
            }
        }

        for(int i = 0; i < aboutFeatures.length; ++i) {
            aboutFeaturesList.add(new ArrayList<Features>());
            for(int k = 0; k < aboutFeatures[i].length; ++k) {
                aboutFeaturesList.get(i).add(aboutFeatures[i][k]);
            }
        }
    }

    public int distance(ArrayList<Features> a, ArrayList<Features> b) {

        int[] costs = new int[b.size() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.size(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.size(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.get(i - 1) == b.get(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.size()];
    }

    public int twoDDistance(ArrayList<ArrayList<Features>> a, ArrayList<ArrayList<Features>> b) {
        int sum = 0;

        for(int i = 0; i < a.size(); ++i) {
            sum = sum + distance(a.get(i), b.get(i));
        }
        return sum;
     }

    public Gestures classify(ArrayList<ArrayList<Features>> features) {
        int copDistance = twoDDistance(features, copFeaturesList);
        int headDistance = twoDDistance(features, headFeaturesList);
        int hungryDistance = twoDDistance(features, hungryFeaturesList);
        int aboutDistance = twoDDistance(features, aboutFeaturesList);


        if(copDistance == (Math.min(copDistance, Math.min(headDistance, Math.min(hungryDistance, aboutDistance))))) {
            return Gestures.COP;
        } else if (headDistance == (Math.min(copDistance, Math.min(headDistance, Math.min(hungryDistance, aboutDistance)))))  {
            return Gestures.HEAD;
        } else if (hungryDistance == (Math.min(copDistance, Math.min(headDistance, Math.min(hungryDistance, aboutDistance))))){
            return Gestures.HUNGRY;
        } else if (aboutDistance == (Math.min(copDistance, Math.min(headDistance, Math.min(hungryDistance, aboutDistance))))) {
            return Gestures.ABOUT;
        } else {
            return Gestures.NONE;
        }
    }
}