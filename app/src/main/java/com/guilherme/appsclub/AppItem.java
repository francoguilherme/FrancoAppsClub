package com.guilherme.appsclub;

import android.graphics.Bitmap;

public class AppItem {

    private Bitmap image;
    private String appName;
    private String company;
    private String description;
    private String score;
    private String countries;

    public AppItem(Bitmap image, String appName, String company, String description, String score, String countries){

        super();
        this.image = image;
        this.appName = appName;
        this.company = company;
        this.description = description;
        this.score = score;
        this.countries = countries;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }
}
