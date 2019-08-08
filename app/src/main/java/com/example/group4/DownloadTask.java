package com.example.group4;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... strings) {
        try{
            //connect to the server
            Log.i("info", "start connection");
            URL url = new URL("http://ec2-174-129-110-220.compute-1.amazonaws.com/uploads/group4");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            //check connection
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();

            //transfer data
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(strings[0]);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return "cancelled";
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "completed";
    }
}
