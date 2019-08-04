package com.example.group4;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

class DynamicXYDatasource implements Runnable {

    // encapsulates management of the observers watching this datasource for update events:
    class Notifier extends Observable {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    }

    private static final int SAMPLE_SIZE = 40;
    private Notifier notifier;
    private boolean keepRunning = false;
    private Random rand = new Random();
    private boolean postData = false;

    {
        notifier = new Notifier();
    }

    public void startData() {
        postData = true;
    }

    public void stopData() {
        postData = false;
    }

    public void stopThread() {
        keepRunning = false;
    }

    //@Override
    public void run() {
        try {
            keepRunning = true;
            while (keepRunning) {
                Thread.sleep(1000);
                if (postData) {
                    notifier.notifyObservers();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getItemCount(int series) {
        return SAMPLE_SIZE;
    }

    public Number getX(int series, int index) {
        if (index >= SAMPLE_SIZE) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public Number getY(int series, int index) {
        if (index >= SAMPLE_SIZE) {
            throw new IllegalArgumentException();
        }
        return rand.nextDouble() * 10.0;
    }

    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }

}
