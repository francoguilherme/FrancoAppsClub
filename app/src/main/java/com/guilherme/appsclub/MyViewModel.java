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

class MyViewModel extends ViewModel {

    private String countryCode = "";

    ArrayList<AppItem> appItems = new ArrayList<>();
    public ArrayList<String> imageURLs = new ArrayList<>();
    private MutableLiveData<ArrayList<AppItem>> apps;

    LiveData<ArrayList<AppItem>> getApps(){

        if (apps == null){
            apps = new MutableLiveData<>();
            loadApps();
        }
        return apps;
    }

    public void setCountryCode(String s) {

        countryCode = s;
    }

    private void loadApps(){

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

                    appItems.add(new AppItem(
                            jsonPart.getString("imageURL"),
                            jsonPart.getString("name"),
                            jsonPart.getString("company"),
                            jsonPart.getString("description"),
                            jsonPart.getString("score")
                    ));
                }

                apps.postValue(appItems);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
