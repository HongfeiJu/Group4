package com.example.group4;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import android.graphics.DashPathEffect;

import java.util.Observable;
import java.util.Observer;

public class GraphFragment extends Fragment implements View.OnClickListener {

    private class PlotUpdater implements Observer {
        Plot plot;

        public PlotUpdater(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void update(Observable o, Object arg) {
            plot.redraw();
        }
    }

    //define View, graph, local variables
    private View view;
    private XYPlot graph;
    private Button start, stop;
    private DynamicSeries seriesX, seriesY, seriesZ;
    private LineAndPointFormatter xValueFormatter, yValueFormatter, zValueFormatter;
    private int count = 1;

    //create random, thread handler, and thread toggle boolean
    private boolean running;
    private PlotUpdater plotUpdater;
    DynamicXYDatasource data;
    private Thread thread;

    //create graph fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize graph, start and stop buttons
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        graph = (XYPlot) view.findViewById(R.id.graph);
        start = (Button) view.findViewById(R.id.btn_start);
        stop = (Button) view.findViewById(R.id.btn_stop);

        //create listeners for start and stop
        start.setOnClickListener((View.OnClickListener) this);
        stop.setOnClickListener((View.OnClickListener) this);

        //create new data series
        data = new DynamicXYDatasource();
        seriesX = new DynamicSeries(data,0, "X Values");
        seriesY = new DynamicSeries(data, 1, "Y Values");
        seriesZ = new DynamicSeries(data, 2, "Z Values");

        //create new random, handler, set thread toggle to false.
        running = false;
        plotUpdater = new PlotUpdater(graph);

        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("0"));

        //set line format and add data to graph
        xValueFormatter = new LineAndPointFormatter(
                Color.rgb(0, 200, 0), null, null, null);
        xValueFormatter.getLinePaint().setStrokeWidth(10);
        xValueFormatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);

        yValueFormatter = new LineAndPointFormatter(
                Color.rgb(200,0,0), null, null, null);
        yValueFormatter.getLinePaint().setStrokeWidth(10);
        yValueFormatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);

        zValueFormatter = new LineAndPointFormatter(Color.rgb(0,0,200),
                null, null, null);
        zValueFormatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        zValueFormatter.getLinePaint().setStrokeWidth(10);

        //watch for graph updates
        data.addObserver(plotUpdater);

        //define graph properties
        graph.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        graph.setDomainStepValue(5);

        graph.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

        graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("###.###"));

        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        graph.getGraph().getDomainGridLinePaint().setPathEffect(dashFx);
        graph.getGraph().getRangeGridLinePaint().setPathEffect(dashFx);

        //create new database
        Context context = getActivity().getApplicationContext();
        PatientDatabase db = Room.databaseBuilder(context, PatientDatabase.class,
                "patient_database").build();

        return view;
    }

    //use super class onResume and onPause
    @Override
    public void onResume() {
        super.onResume();
        thread = new Thread();
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        data.stopThread();
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
        if (v.getId() == R.id.btn_start) {
            Log.i("info", "start");
            if (running) return;
            running = true;
            graph.addSeries(seriesX, xValueFormatter);
            graph.addSeries(seriesY, yValueFormatter);
            graph.addSeries(seriesZ, zValueFormatter);
            data.startData();

        } else if (v.getId() == R.id.btn_stop) {
            Log.i("info", "stop");
            if (!running) return;
            running = false;
            //handler.removeCallbacksAndMessages(null);
            data.stopData();
            graph.removeSeries(seriesX);
            graph.removeSeries(seriesY);
            graph.removeSeries(seriesZ);
            graph.clear();
        }
    }
}
