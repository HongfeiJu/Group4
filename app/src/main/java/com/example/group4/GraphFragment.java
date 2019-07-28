package com.example.group4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.Random;

public class GraphFragment extends Fragment implements View.OnClickListener {
	//define View, graph, local variables
    private View view;
    private GraphView graph;
    private Button start, stop;
    private LineGraphSeries<DataPoint> series;
    private int count=1;

	//create random, thread handler, and thread toggle boolean
    private Random rand;
    boolean running;
    private Handler handler;

	//create graph fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//initialize graph, start and stop buttons
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);
        start = (Button) view.findViewById(R.id.btn_start);
        stop = (Button) view.findViewById(R.id.btn_stop);

		//create listeners for start and stop
        start.setOnClickListener((View.OnClickListener) this);
        stop.setOnClickListener((View.OnClickListener) this);

		//create new data series
        series = new LineGraphSeries<>();

		//create new random, handler, set thread toggle to false.
        rand = new Random();
        running = false;
        handler = new Handler();

		//add data series to graph, set axis bounds and labels.
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Series Index");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Random Values");

        return view;
    }

	//use super class onResume and onPause
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                sensorHandler.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

	/*if a click occurs, check to see if it was from the start
	or stop buttons.  If start, then check if the thread is already
	running, if no, add series to graph and add generateData to
	message queue.
	If a click on stop, check for running thread, stop if running
	and remove data series from graph (clears graph) and clear
	message queue.

	onClick ignores redundant clicks.*/
    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.btn_start){
            Log.i("info", "start");
            if(running) return;
            running=true;
            graph.addSeries(series);
            handler.post(generateData);
        }else if(v.getId()== R.id.btn_stop){
            Log.i("info", "stop");
            if(!running) return;
            running=false;
            handler.removeCallbacksAndMessages(null);
            graph.removeAllSeries();
        }
    }

	/*generateData thread.  run() updates log, adds a new data point to the 
	data series, updates index and waits 0.5 secs before posting generateData
	to the message queue*/
    Runnable generateData = new Runnable() {
        @Override
        public void run() {
            Log.d("info", "generating data");

            handler.postDelayed(generateData, 500);
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("acData");

            String[] vals = msg.split(" ");
            Log.i("info"," receive data "+ Arrays.toString(vals));
            series.appendData(new DataPoint(count+1, Float.parseFloat(vals[1])), true, 40);
            count++;
        }
    };
}
