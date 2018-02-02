package com.guilherme.appsclub;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FindCountryTask extends AsyncTask<String, Void, String> {

    private StringBuilder apiData = new StringBuilder();
    private String line = "";
    private String countryCode;

    public String getCountryCode() {
        return countryCode;
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

        try {

            JSONObject jsonObject = new JSONObject(result);
            countryCode = jsonObject.getString("countryCode");
            countryCode = countryCode.toLowerCase();
            Log.i("Country from api", countryCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}