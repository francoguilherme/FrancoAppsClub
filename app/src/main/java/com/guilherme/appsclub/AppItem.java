package com.guilherme.appsclub;

import android.graphics.Bitmap;

public class AppItem {

    private Bitmap image;
    private String appName;
    private String company;
    private String description;
    private String score;

    public AppItem(Bitmap image, String appName, String company, String description, String score){

        super();
        this.image = image;
        this.appName = appName;
        this.company = company;
        this.description = description;
        this.score = score;
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

    public String getCompany() {
        return company;
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
}
