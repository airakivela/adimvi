package com.application.adimviandroid.utils;

import android.os.AsyncTask;

import com.application.adimviandroid.models.RoomMessageModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil  extends AsyncTask<RoomMessageModel, Integer, String> {

    public DownloadUtil() {

    }

    @Override
    protected String doInBackground(RoomMessageModel... models) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(models[0].extra);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            String[] fileSplit = models[0].extra.split("/");
            String fileName = fileSplit[fileSplit.length - 1];
            output = new FileOutputStream(DirManager.voiceoiceMessageDir(models[0].userID) + "/" + fileName);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;

                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {

            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(String s) {

    }
}
