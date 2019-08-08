package com.example.group4;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        Log.i("info", "start upload task");
        String sourceFileUri = strings[0];
        String serverURL = "http://ec2-174-129-110-220.compute-1.amazonaws.com/UploadToServer.php";
        final MediaType MEDIA_TYPE = MediaType.parse("multipart/form-data");

        final OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upfile", sourceFileUri,
                        RequestBody.create(MEDIA_TYPE, new File(sourceFileUri)))
                .build();

        Request request = new Request.Builder()
                .url(serverURL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
        } catch (Exception e) {
            Log.e("Upload File", e.getMessage());
        }

        return null;
    }
}
