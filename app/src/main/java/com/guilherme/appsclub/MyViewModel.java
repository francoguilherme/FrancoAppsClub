package com.guilherme.appsclub;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

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

class MyViewModel extends ViewModel {

    private String countryCode;
    private TelephonyManager telephonyManager;

    ArrayList<AppItem> appItems = new ArrayList<>();
    private ArrayList<String> imageURLs = new ArrayList<>();

    private MutableLiveData<ArrayList<AppItem>> apps;
    LiveData<ArrayList<AppItem>> getApps(){

        if (apps == null){
            apps = new MutableLiveData<>();
            loadApps();
        }
        return apps;
    }

    private void loadApps(){

        findCountryCode();

        StringBuilder url = new StringBuilder();
        url.append("https://private-291f64-appsclub1.apiary-mock.com/")
                .append(countryCode)
                .append("/")
                .append(BuildConfig.FLAVOR);

        new DownloadApiDataTask().execute(url.toString());
    }

    public class DownloadApiDataTask extends AsyncTask<String, Void, String> {

        StringBuilder apiData = new StringBuilder();
        String line = "";

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

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            apps.postValue(appItems);
        }
    }

    public void setTelephonyManager(TelephonyManager tm) {

        telephonyManager = tm;
    }

    private void findCountryCode(){

        // Finds the user's ISO country code
        countryCode = telephonyManager.getNetworkCountryIso();

        if (countryCode != null){
            return; // If code was already found, move on
        }

        FindCountryTask findCountryTask = new FindCountryTask();
        findCountryTask.execute("http://ip-api.com/json");
        this.countryCode = findCountryTask.getCountryCode();
    }

    private void addBitmapsToApps(){

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
