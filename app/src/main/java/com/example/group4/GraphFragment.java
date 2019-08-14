package com.example.group4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GraphFragment extends Fragment implements View.OnClickListener {
	//define View, graph, local variables
    private View view;
    private GraphView graph;
    private Button copCollect, copAlgo, hungryCollect, hungryAlgo, headacheCollect,
    headacheAlgo, aboutCollect, aboutAlgo;
    private TextView result;
    private LineGraphSeries<DataPoint> seriesX, seriesY, seriesZ;
    private LineGraphSeries<DataPoint> gyroX, gyroY, gyroZ;
    private int acclcount = 1, gyrocount = 1, count = 1;
    private ArrayList<ArrayList<Float>> dynamicData;
    private ArrayList<ArrayList<Float>> sensorData;
    private TextToSpeech textToSpeech;
    private DataSmoother smoother = new DataSmoother();
    private final int num_sensor = 6;
    private boolean inCollecting = false;
	//create random, thread handler, and thread toggle boolean
    boolean running;

    Intent sersorIntent, gyroSensorIntent;

    ArrayList<Pair<Gestures, Gestures>> trials;
    FindFeatures findFeatures;
    Classifier classifier;

	//create graph fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//initialize graph, start and stop buttons
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);
        copCollect = (Button) view.findViewById(R.id.btn_cop_collect);
        copAlgo = (Button) view.findViewById(R.id.btn_cop_algorithm);
        hungryCollect = (Button) view.findViewById(R.id.btn_hungry_collect);
        hungryAlgo = (Button) view.findViewById(R.id.btn_hungry_algorithm);
        headacheCollect = (Button) view.findViewById(R.id.btn_headache_collect);
        headacheAlgo = (Button) view.findViewById(R.id.btn_headache_algorithm);
        aboutCollect = (Button) view.findViewById(R.id.btn_about_collect);
        aboutAlgo = (Button) view.findViewById(R.id.btn_about_algorithm);
        result = (TextView) view.findViewById(R.id.result);

        //create listeners for start and stop
        copCollect.setOnClickListener((View.OnClickListener) this);
        copAlgo.setOnClickListener((View.OnClickListener) this);
        hungryCollect.setOnClickListener((View.OnClickListener) this);
        hungryAlgo.setOnClickListener((View.OnClickListener) this);
        headacheCollect.setOnClickListener((View.OnClickListener) this);
        headacheAlgo.setOnClickListener((View.OnClickListener) this);
        aboutCollect.setOnClickListener((View.OnClickListener) this);
        aboutAlgo.setOnClickListener((View.OnClickListener) this);

		//create new data series and set the colors
        seriesX = new LineGraphSeries<>();
        seriesY = new LineGraphSeries<>();
        seriesZ = new LineGraphSeries<>();
        gyroX = new LineGraphSeries<>();
        gyroY = new LineGraphSeries<>();
        gyroZ = new LineGraphSeries<>();
        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.BLUE);
        gyroX.setColor(Color.CYAN);
        gyroY.setColor(Color.BLACK);
        gyroZ.setColor(Color.MAGENTA);

        dynamicData = new ArrayList<ArrayList<Float>>(num_sensor);
        sensorData = new ArrayList<ArrayList<Float>>(num_sensor);

        for(int i = 0; i < num_sensor; ++i) {
            dynamicData.add(new ArrayList<Float>());
            sensorData.add(new ArrayList<Float>());
        }

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int res = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("info", "language not supported");
                    }
                }
            }
        });

		//create new random, handler, set thread toggle to false.
        running = false;


        //initialize accelerometer senor
        sersorIntent = new Intent(getActivity(), AcclSensorHandler.class);
        gyroSensorIntent = new Intent(getActivity(), GyroSensorHandler.class);

		//add data series to graph, set axis bounds and labels.
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Series Index");
        graph.getGridLabelRenderer().setVerticalAxisTitle("");

        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
        graph.addSeries(gyroX);
        graph.addSeries(gyroY);
        graph.addSeries(gyroZ);
        getActivity().startService(sersorIntent);
        getActivity().startService(gyroSensorIntent);

        trials = new ArrayList<>();
        findFeatures = new FindFeatures();
        classifier = new Classifier();

        return view;
    }

    //use super class onResume and onPause
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                AcclSensorHandler.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void returnResults(Gestures g) {
        double positive = 0;
        double pcount = 0;
        double fpositive = 0;
        double fposrate = 0;
        double posrate = 0;
        Log.i("Trials:", trials.toString());
        for(int i = 0; i < trials.size(); ++i) {
            if(trials.get(i).first == g) {
                pcount = pcount + 1;
                if(trials.get(i).second == g) {
                    positive = positive + 1;
                }
            } else {
                if(trials.get(i).second == g) {
                    fpositive = fpositive + 1;
                }
            }
        }

        fposrate = fpositive / (trials.size() - pcount);
        posrate = positive / pcount;

        Log.i("Pos", Double.toString(positive));
        Log.i("pcount", Double.toString(pcount));
        Log.i("fpos", Double.toString(fpositive));

        result.setText("");
        result.setText("True positive rate: "+Double.toString(posrate)+"\nFalse positive rate: "+Double.toString(fposrate));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_cop_collect:
                collectData(sensorData, Gestures.COP);
                break;
            case R.id.btn_hungry_collect:
                collectData(sensorData, Gestures.HUNGRY);
                break;
            case R.id.btn_headache_collect:
                collectData(sensorData, Gestures.HEAD);
                break;
            case R.id.btn_about_collect:
                collectData(sensorData, Gestures.ABOUT);
                break;
            case R.id.btn_cop_algorithm:
                returnResults(Gestures.COP);
                break;
            case R.id.btn_hungry_algorithm:
                returnResults(Gestures.HUNGRY);
                break;
            case R.id.btn_headache_algorithm:
                returnResults(Gestures.HEAD);
                break;
            case R.id.btn_about_algorithm:
                returnResults(Gestures.ABOUT);
                break;

            default:
                Log.i("onClick", "Non-button clicked");
        }
    }


    private void collectData(final ArrayList<ArrayList<Float>> targetData, final Gestures gesture) {
        speakText("get ready in 3 second");
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                speakText("start");
            }
        }, 5000);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                ArrayList<ArrayList<Features>> f = new ArrayList<>();
                for(int i = 0; i < num_sensor; ++i) {
                    int len = dynamicData.get(i).size();
                    targetData.get(i).clear();
                    targetData.get(i).addAll(dynamicData.get(i).subList(Math.max(0, len - 50), len));
                    smoother.smooth(targetData.get(i),2);
                }
                f.addAll(findFeatures.tagAllFeatures(targetData));
                trials.add(new Pair<Gestures, Gestures>(gesture, classifier.classify(f)));
            }
        }, 10000);
    }

    //receive data from the accelerometer service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] acclvals;
            String[] gyrovals;
            String acclmsg = intent.getStringExtra("acData");
            String gyromsg = intent.getStringExtra("gyroData");

            if(acclmsg!=null){
                acclvals = acclmsg.split(" ");
                //Log.i("info"," receive data "+ Arrays.toString(vals));
                seriesX.appendData(new DataPoint(count+1, Float.parseFloat(acclvals[0])), true, 40);
                seriesY.appendData(new DataPoint(count+1, Float.parseFloat(acclvals[1])), true, 40);
                seriesZ.appendData(new DataPoint(count+1, Float.parseFloat(acclvals[2])), true, 40);
                dynamicData.get(0).add(Float.parseFloat(acclvals[0]));
                dynamicData.get(1).add(Float.parseFloat(acclvals[1]));
                dynamicData.get(2).add(Float.parseFloat(acclvals[2]));
                acclcount++;
            }
            if(gyromsg!=null){
                gyrovals = gyromsg.split(" ");
                gyroX.appendData(new DataPoint(count+1, Float.parseFloat(gyrovals[0])), true, 40);
                gyroY.appendData(new DataPoint(count+1, Float.parseFloat(gyrovals[1])), true, 40);
                gyroZ.appendData(new DataPoint(count+1, Float.parseFloat(gyrovals[2])), true, 40);
                dynamicData.get(3).add(Float.parseFloat(gyrovals[0]));
                dynamicData.get(4).add(Float.parseFloat(gyrovals[1]));
                dynamicData.get(5).add(Float.parseFloat(gyrovals[2]));
                gyrocount++;
            }

            while (dynamicData.size()>80) dynamicData.remove(0);
            if (acclcount == gyrocount) {
                count++;
            }
        }
    };

    private void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

}
