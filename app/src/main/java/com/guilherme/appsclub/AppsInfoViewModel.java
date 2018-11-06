package com.guilherme.appsclub;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

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

public class AppsInfoViewModel extends ViewModel {

    public ArrayList<AppItem> appItems = new ArrayList<>();
    public ArrayList<String> imageURLs = new ArrayList<>();

    private MutableLiveData<ArrayList<AppItem>> appsData;

    LiveData<ArrayList<AppItem>> getAppsData(){

        if (appsData == null){
            appsData = new MutableLiveData<>();
            loadApps();
        }
        return appsData;
    }

    private String countryCode = "";

    public void setCountryCode(String s) {

        countryCode = s;
    }

    private void loadApps(){

        StringBuilder url = new StringBuilder();
        // Used to specify the user's country and the app flavor (Apps, Kids or Games)
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
        protected void onPostExecute(String downloadedApiData) {
            super.onPostExecute(downloadedApiData);

            try {

                JSONArray appsArray = new JSONArray(downloadedApiData);

                for (int i = 0; i < appsArray.length(); i++){

                    JSONObject appInfo = appsArray.getJSONObject(i);

                    imageURLs.add(appInfo.getString("imageURL"));

                    appItems.add(new AppItem(
                            appInfo.getString("imageURL"),
                            appInfo.getString("name"),
                            appInfo.getString("company"),
                            appInfo.getString("description"),
                            appInfo.getString("score")
                    ));
                }

                appsData.postValue(appItems);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
