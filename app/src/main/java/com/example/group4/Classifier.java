package com.example.group4;

import java.util.ArrayList;

enum Gestures {
    COP, HEAD, HUNGRY, ABOUT, NONE
}

public class Classifier {

    private Features[][] copFeatures = { {} };
    private Features[][] headFeatures = { {} };
    private Features[][] hungryFeatures = { {} };
    private Features[][] aboutFeatures = { {} };

    ArrayList<ArrayList<Features>> copFeaturesList;
    ArrayList<ArrayList<Features>> headFeaturesList;
    ArrayList<ArrayList<Features>> hungryFeaturesList;
    ArrayList<ArrayList<Features>> aboutFeaturesList;


    public Classifier() {
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
        int headDitance = twoDDistance(features, headFeaturesList);
        int hungryDistance = twoDDistance(features, hungryFeaturesList);
        int aboutDistance = twoDDistance(features, aboutFeaturesList);

        if((copDistance < headDitance) &&
                (copDistance < hungryDistance) &&
                (copDistance < aboutDistance)) {
            return Gestures.COP;
        } else if ((headDitance < copDistance) &&
                (headDitance < hungryDistance) &&
                (headDitance < aboutDistance)) {
            return Gestures.HEAD;
        } else if ((hungryDistance < copDistance) &&
                (hungryDistance < headDitance) &&
                (hungryDistance < aboutDistance)) {
            return Gestures.HUNGRY;
        } else if ((aboutDistance < copDistance) &&
                (aboutDistance < hungryDistance) &&
                (aboutDistance < headDitance)) {
            return Gestures.ABOUT;
        } else {
            return Gestures.NONE;
        }
    }
}