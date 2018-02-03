package com.guilherme.appsclub;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// Holds and manages Async Tasks so they may work even with configuration changes

public class TaskFragment extends Fragment{

    public ArrayList<AppItem> appItems = new ArrayList<>();
    public ArrayList<String> imageURLs = new ArrayList<>();
    private String countryCode;

    // Callback interface used to report the task's progress to the MainAcivity
    interface TaskCallbacks{

        void onPreExecute();
        void onPostExecute();
    }

    private TaskCallbacks callbacks;
    private DownloadApiDataTask downloadApiDataTask;

    // Hold a reference to the parent context to report task's progress and results
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (TaskCallbacks) context;
    }

    // This is only called once when the fragment is created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);

        // This will be used to filter which apps to show
        countryCode = getArguments().getString("countryCode");

        // Create and execute the background task
        downloadApiDataTask = new DownloadApiDataTask();

        try {

            StringBuilder url = new StringBuilder();
            url.append("https://private-291f64-appsclub1.apiary-mock.com/")
                    .append(countryCode)
                    .append("/")
                    .append( BuildConfig.FLAVOR);

            downloadApiDataTask.execute(url.toString()).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //Set the callback to null to not accidentally leak the Activity instance.
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public class DownloadApiDataTask extends AsyncTask<String, Void, String> {

        StringBuilder apiData = new StringBuilder();
        String line = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (callbacks != null){
                callbacks.onPreExecute();
            }
        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = streamReader.readLine()) != null){

                    apiData.append(line);
                }

                return apiData.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (callbacks != null){
                callbacks.onPostExecute();
            }

            try {

                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++){

                    JSONObject jsonPart = array.getJSONObject(i);

                    imageURLs.add(jsonPart.getString("imageURL"));

                    appItems.add(new AppItem(null, // Null because we haven't downloaded the app image yet
                            jsonPart.getString("name"),
                            jsonPart.getString("company"),
                            jsonPart.getString("description"),
                            jsonPart.getString("score")
                    ));
                }

                addBitmapsToApps();
                callbacks.onPostExecute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                Bitmap appBitmap = BitmapFactory.decodeStream(inputStream);

                return appBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void addBitmapsToApps(){

        Bitmap appImage;

        for (int i = 0; i < appItems.size(); i++){

            try{

                appImage = new DownloadImagesTask().execute(imageURLs.get(i)).get();
                appItems.get(i).setImage(appImage);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
