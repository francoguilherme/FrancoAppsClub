package com.guilherme.appsclub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView appImageView;
    private TextView descriptionView;
    private TextView companyView;
    private RatingBar scoreBar;

    private String appName;
    private Bitmap image;
    private String description;
    private String company;
    private String score;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void findViews(){

        toolbar = findViewById(R.id.toolbar);
        appImageView = findViewById(R.id.appImage);
        descriptionView = findViewById(R.id.descriptionView);
        companyView = findViewById(R.id.companyView);
        scoreBar = findViewById(R.id.scoreBar);
    }

    public void getExtras(){

        Intent parentIntent = getIntent();

        appName = parentIntent.getStringExtra("appName");
        image = parentIntent.getParcelableExtra("image");
        description = parentIntent.getStringExtra("description");
        company = parentIntent.getStringExtra("company");
        score = parentIntent.getStringExtra("score");
    }

    public void updateDetails(){

        toolbar.setTitle(appName);
        appImageView.setImageBitmap(image);
        descriptionView.setText(description);
        companyView.setText(company);
        scoreBar.setRating(Float.valueOf(score));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        findViews();
        getExtras();
        updateDetails();

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Download", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
