package com.example.group4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class GraphFragment extends Fragment implements View.OnClickListener {
	//define View, graph, local variables
    private View view;
    private GraphView graph;
    private Button start, stop, upload, download;
    private LineGraphSeries<DataPoint> seriesX, seriesY, seriesZ;
    private int count=1;
    private EditText patientID, age, patientName;
    private RadioGroup sex;

	//create random, thread handler, and thread toggle boolean
    private Random rand;
    boolean running;
    private Handler handler;

    Intent sersorIntent;

    SQLiteDatabase db;

	//create graph fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//initialize graph, start and stop buttons
        view = inflater.inflate(R.layout.graph_fragment, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);
        start = (Button) view.findViewById(R.id.btn_start);
        stop = (Button) view.findViewById(R.id.btn_stop);
        upload = (Button) view.findViewById(R.id.btn_upload);
        download = (Button) view.findViewById(R.id.btn_download);
        patientID = (EditText)  view.findViewById(R.id.PatientID);
        age = (EditText) view.findViewById(R.id.age);
        patientName = (EditText) view.findViewById(R.id.patient_name);
        sex = (RadioGroup)  view.findViewById(R.id.sexes);

        //create listeners for start and stop
        start.setOnClickListener((View.OnClickListener) this);
        stop.setOnClickListener((View.OnClickListener) this);
        upload.setOnClickListener((View.OnClickListener) this);
        download.setOnClickListener((View.OnClickListener) this);

		//create new data series
        seriesX = new LineGraphSeries<>();
        seriesY = new LineGraphSeries<>();
        seriesZ = new LineGraphSeries<>();

        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.BLUE);

		//create new random, handler, set thread toggle to false.
        rand = new Random();
        running = false;
        handler = new Handler();

        sersorIntent = new Intent(getActivity(), sensorHandler.class);

		//add data series to graph, set axis bounds and labels.
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Series Index");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Random Values");

        //create database if not exist
        db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/Documents/group4", null);

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
            graph.addSeries(seriesX);
            graph.addSeries(seriesY);
            graph.addSeries(seriesZ);
            createTable();
            getActivity().startService(sersorIntent);

        }else if(v.getId()== R.id.btn_stop){

            Log.i("info", "stop");
            if(!running) return;
            running=false;
            getActivity().stopService(sersorIntent);
            graph.removeAllSeries();

        }else if(v.getId() == R.id.btn_upload){

            Log.i("info", "upload");
            uploadDatabase();

        }else if(v.getId() == R.id.btn_download){

            Log.i("info", "download");
            downloadDatabase();

        }
    }

    private void createTable() {
        db.beginTransaction();
        try {
            //perform your database operations here ...
            String tableName = getTable();
            Log.i("info", "create table "+tableName);

            db.execSQL("create table if not exists " + tableName + " ("
                    + " time integer, "
                    + " x float, "
                    + " y float, "
                    + " z float ); " );

            db.setTransactionSuccessful(); //commit your changes
        }catch (SQLiteException e) {
            //report problem
        }finally {
            db.endTransaction();
        }
    }

    private String getTable() {
        String id = patientID.getText().toString(),
                a = age.getText().toString(),
                name = patientName.getText().toString(), s="";
        int checkedID = sex.getCheckedRadioButtonId();
        if(checkedID == R.id.male) s = "male";
        else if(checkedID == R.id.female) s = "female";
        return name+"_"+id+"_"+a+"_"+s;
    }

    private void uploadDatabase() {
        new UploadTask().execute();
    }

    private void downloadDatabase(){
        new DownloadTask().execute();
        /*
        try {

            String res = new DownloadTask().execute().get();
            if(res.equals("completed")){

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        //declare data series for downloaded data
//        LineGraphSeries<DataPoint> tempSeriesX, tempSeriesY, tempSeriesZ;
//        tempSeriesX = new LineGraphSeries<>();
//        tempSeriesY = new LineGraphSeries<>();
//        tempSeriesZ = new LineGraphSeries<>();
//
//        tempSeriesX.setColor(Color.RED);
//        tempSeriesY.setColor(Color.GREEN);
//        tempSeriesZ.setColor(Color.BLUE);
//
//        //read downloaded database file
//        SQLiteDatabase tempdb = SQLiteDatabase.openOrCreateDatabase(
//                Environment.getExternalStorageDirectory()+"/Database/group4", null);
//
//        //get the last ten row
//        Cursor res = tempdb.rawQuery( "select * from " + getTable()
//                + " order by time desc limit 10", null );
//
//        //put data into the series
//        res.moveToLast();
//        int count = 10;
//        while(res.isBeforeFirst() == false && count>0) {
//            int time = res.getInt(res.getColumnIndex("time"));
//            tempSeriesX.appendData(new DataPoint(time, res.getFloat(res.getColumnIndex("x"))), true, 40);
//            tempSeriesY.appendData(new DataPoint(time, res.getFloat(res.getColumnIndex("y"))), true, 40);
//            tempSeriesZ.appendData(new DataPoint(time, res.getFloat(res.getColumnIndex("z"))), true, 40);
//            res.moveToPrevious();
//            count--;
//        }
//        graph.removeAllSeries();
//        graph.addSeries(tempSeriesX);
//        graph.addSeries(tempSeriesY);
//        graph.addSeries(tempSeriesZ);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("acData");

            String[] vals = msg.split(" ");
            Log.i("info"," receive data "+ Arrays.toString(vals));
            seriesX.appendData(new DataPoint(count+1, Float.parseFloat(vals[0])), true, 40);
            seriesY.appendData(new DataPoint(count+1, Float.parseFloat(vals[1])), true, 40);
            seriesZ.appendData(new DataPoint(count+1, Float.parseFloat(vals[2])), true, 40);

            //insert data to database
            insertRow(count+1, Float.parseFloat(vals[0]), Float.parseFloat(vals[1]), Float.parseFloat(vals[2]));
            count++;
        }
    };

    private void insertRow(int time, float x, float y, float z){
        db.beginTransaction();
        try {
            //perform your database operations here ...
            String tableName = getTable();
            Log.i("info", "insert row");
            db.execSQL("insert into " + tableName + " (time, x, y, z) values ("
                    + count + ", " + x + ", " + y + ", " + z + ")");
            db.setTransactionSuccessful(); //commit your changes
        }catch (SQLiteException e) {
            //report problem
        }finally {
            db.endTransaction();
        }
    }



}
