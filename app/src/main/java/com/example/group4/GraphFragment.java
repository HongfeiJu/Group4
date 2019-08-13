package com.example.group4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
    private int count=1;
    private List<float[]> dynamicData;
    private float gyroX = 0, gyroY = 0, gyroZ =0;
    private List<List<float[]>> copData, hungryData, headacheData, aboutData;
    private TextToSpeech textToSpeech;

	//create random, thread handler, and thread toggle boolean
    boolean running;

    Intent sersorIntent, gyroSensorIntent;

	//create graph fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//initialize graph, start and stop buttons
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);
        copCollect = (Button) view.findViewById(R.id.btn_cop_collect);
        copAlgo = (Button) view.findViewById(R.id.btn_cop_algo);
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
        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.BLUE);

        dynamicData = new ArrayList<>();
        copData = new ArrayList<>();
        hungryData = new ArrayList<>();
        headacheData = new ArrayList<>();
        aboutData = new ArrayList<>();

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
        graph.getGridLabelRenderer().setVerticalAxisTitle("Random Values");

        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
        getActivity().startService(sersorIntent);
        getActivity().startService(gyroSensorIntent);

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

    @Override
    public void onClick(View v) {

        if(v.getId()== R.id.btn_cop_collect){
            collectData(copData);


        }else if(v.getId()== R.id.btn_cop_algo){
            analyzeCop();

        }else if(v.getId()== R.id.btn_hungry_collect){
            collectData(copData);

        }else if(v.getId()== R.id.btn_hungry_algorithm){
            analyzeHungry();

        }else if(v.getId()== R.id.btn_headache_collect){
            collectData(headacheData);

        }else if(v.getId()== R.id.btn_headache_algorithm){
            anaylzeHeadache();

        }else if(v.getId()== R.id.btn_about_collect){
            collectData(aboutData);

        }else if(v.getId()== R.id.btn_about_algorithm){
            analyzeAbout();

        }
    }



    private void collectData(final List<List<float[]>> targetData) {
        speakText("get ready in 3 second");
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                speakText("start");
            }
        }, 5000);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                int len=dynamicData.size();
                List<float[]> data=new ArrayList<>();
                data.addAll(dynamicData.subList(Math.max(0, len-50), len));
                targetData.add(data);
                if(targetData.size()>4) targetData.remove(0);
                for(float[] e: data){
                    Log.i("info", Arrays.toString(e));
                }
            }
        }, 10000);
    }

    private void analyzeCop() {
        int copSize = copData.size(), otherSize=hungryData.size()+headacheData.size()+aboutData.size();
        String tp="", fp="";
        if(copSize==0){
            tp="NA";
        }else{
            int positiveCount=copValid(copData);
            tp=Double.toString(positiveCount*1.0/copSize);
        }

        if(otherSize==0){
            fp="NA";
        }else{
            int positiveCount=copValid(hungryData)+copValid(headacheData)+copValid(aboutData);
            fp=Double.toString(positiveCount*1.0/otherSize);
        }
        result.setText("cop algo: true positive: "+tp+", false positive: "+fp);
    }

    private int copValid(List<List<float[]>> dataSet) {
        int positiveCount=0;
        for(List<float[]> data: dataSet){
            float min1=0, min2=0, min3=0, max1=0, max2=0, max3=0;
            for(float[] e: data){
                min1=Math.min(min1, e[0]); max1=Math.max(max1, e[0]); //x
                min2=Math.min(min2, e[1]); max2=Math.max(max2, e[1]); //y
                min3=Math.min(min3, e[2]); max3=Math.max(max3, e[2]); //z
            }
            float diff1=max1-min1, diff2=max2-min2, diff3=max3-min3;
            if(diff1>20&&diff2<20&&diff3<20
                    ||diff1<20&&diff2>20&&diff3<20
                    ||diff1<20&&diff2<20&&diff3>20) positiveCount++;
        }
        return positiveCount;
    }

    private void anaylzeHeadache() {
        int headacheSize = headacheData.size(), otherSize=hungryData.size()+copData.size()+aboutData.size();
        String tp="", fp="";
        if(headacheSize==0){
            tp="NA";
        }else{
            int positiveCount=headacheValid(headacheData);
            tp=Double.toString(positiveCount*1.0/headacheSize);
        }

        if(otherSize==0){
            fp="NA";
        }else{
            int positiveCount=headacheValid(hungryData)+copValid(copData)+copValid(aboutData);
            fp=Double.toString(positiveCount*1.0/otherSize);
        }
        result.setText("headache algo: true positive: "+tp+", false positive: "+fp);
    }

    private int headacheValid(List<List<float[]>> dataSet) {
        int positiveCount=0;
        for(List<float[]> data: dataSet){
            float min1=0, min2=0, min3=0, max1=0, max2=0, max3=0;
            for(float[] e: data){
                min1=Math.min(min1, e[3]); max1=Math.max(max1, e[3]);
                min2=Math.min(min2, e[4]); max2=Math.max(max2, e[4]);
                min3=Math.min(min3, e[5]); max3=Math.max(max3, e[5]);
            }
            float diff1=max1-min1, diff2=max2-min2, diff3=max3-min3;
            if(diff1>10&&diff2<10&&diff3<10
                    ||diff1<10&&diff2>10&&diff3<10
                    ||diff1<10&&diff2<10&&diff3>10) positiveCount++;
        }
        return positiveCount;
    }

    private void analyzeHungry() {
        int hungrySize = hungryData.size(), otherSize=headacheData.size()+copData.size()+aboutData.size();
        String tp="", fp="";
        if(hungrySize==0){
            tp="NA";
        }else{
            int positiveCount=hungryValid(hungryData);
            tp=Double.toString(positiveCount*1.0/hungrySize);
        }

        if(otherSize==0){
            fp="NA";
        }else{
            int positiveCount=hungryValid(headacheData) + hungryValid(copData) + hungryValid(aboutData);
            fp=Double.toString(positiveCount*1.0/otherSize);
        }
        result.setText("hungry algo: true positive: "+tp+", false positive: "+fp);
    }

    private int hungryValid(List<List<float[]>> dataSet) {
        int positiveCount=0;
        for(List<float[]> data: dataSet){
            float min1=0, min2=0, min3=0, max1=0, max2=0, max3=0;
            for(float[] e: data){
                min1=Math.min(min1, e[3]); max1=Math.max(max1, e[3]);
                min2=Math.min(min2, e[4]); max2=Math.max(max2, e[4]);
                min3=Math.min(min3, e[5]); max3=Math.max(max3, e[5]);
            }
            float diff1=max1-min1, diff2=max2-min2, diff3=max3-min3;
            if(diff1>10&&diff2<10&&diff3<10
                    ||diff1<10&&diff2>10&&diff3<10
                    ||diff1<10&&diff2<10&&diff3>10) positiveCount++;
        }
        return positiveCount;
    }

    private void analyzeAbout() {
        int aboutSize = aboutData.size(), otherSize=headacheData.size()+copData.size()+hungryData.size();
        String tp="", fp="";
        if(aboutSize==0){
            tp="NA";
        }else{
            int positiveCount=aboutValid(aboutData);
            tp=Double.toString(positiveCount*1.0/aboutSize);
        }

        if(otherSize==0){
            fp="NA";
        }else{
            int positiveCount= aboutValid(headacheData)+ aboutValid(copData)+ aboutValid(hungryData);
            fp=Double.toString(positiveCount*1.0/otherSize);
        }
        result.setText("about algo: true positive: "+tp+", false positive: "+fp);
    }

    private int aboutValid(List<List<float[]>> dataSet) {
        int positiveCount=0;
        for(List<float[]> data: dataSet){
            float min1=0, min2=0, min3=0, max1=0, max2=0, max3=0;
            for(float[] e: data){
                min1=Math.min(min1, e[3]); max1=Math.max(max1, e[3]);
                min2=Math.min(min2, e[4]); max2=Math.max(max2, e[4]);
                min3=Math.min(min3, e[5]); max3=Math.max(max3, e[5]);
            }
            float diff1=max1-min1, diff2=max2-min2, diff3=max3-min3;
            if(diff1>10&&diff2<10&&diff3<10
                    ||diff1<10&&diff2>10&&diff3<10
                    ||diff1<10&&diff2<10&&diff3>10) positiveCount++;
        }
        return positiveCount;
    }


    //receive data from the accelerometer service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String acclmsg = intent.getStringExtra("acData"), gyromsg = intent.getStringExtra("gyroData");
            if(acclmsg!=null){
                String[] vals = acclmsg.split(" ");
                //Log.i("info"," receive data "+ Arrays.toString(vals));
                seriesX.appendData(new DataPoint(count+1, Float.parseFloat(vals[0])), true, 40);
                seriesY.appendData(new DataPoint(count+1, Float.parseFloat(vals[1])), true, 40);
                seriesZ.appendData(new DataPoint(count+1, Float.parseFloat(vals[2])), true, 40);
                dynamicData.add(new float[]{Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), Float.parseFloat(vals[2]),
                gyroX, gyroY, gyroZ});
                Log.i("info", Arrays.toString(dynamicData.get(dynamicData.size()-1)));
                while (dynamicData.size()>80) dynamicData.remove(0);
                count++;
            }
            if(gyromsg!=null){
                String[] vals = gyromsg.split(" ");
                gyroX = Float.parseFloat(vals[0]);
                gyroY = Float.parseFloat(vals[1]);
                gyroZ = Float.parseFloat(vals[2]);
            }
        }
    };

    private void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

}
