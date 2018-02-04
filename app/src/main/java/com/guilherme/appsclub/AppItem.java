package com.guilherme.appsclub;

public class AppItem {

    private String imageURL;
    private String appName;
    private String company;
    private String description;
    private String score;

    public AppItem(String imageURL, String appName, String company, String description, String score){

        super();
        this.imageURL = imageURL;
        this.appName = appName;
        this.company = company;
        this.description = description;
        this.score = score;
    }

    public String getImageURL() {
        return imageURL;
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

    public String getScore() {
        return score;
    }
}
