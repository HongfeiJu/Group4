package com.example.group4;

import android.os.AsyncTask;
import android.os.Environment;
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
            Log.i("info", "start connection");
            URL url = new URL("http://ec2-174-129-110-220.compute-1.amazonaws.com/UploadToServer.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.i("info", connection.getResponseMessage());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            Log.i("info", Environment.getExternalStorageDirectory().getPath());
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Documents/group4");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return "cancelled";
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
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
