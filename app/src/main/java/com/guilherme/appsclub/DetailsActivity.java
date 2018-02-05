package com.guilherme.appsclub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView appImageView;
    private TextView descriptionView;
    private TextView companyView;
    private RatingBar scoreBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void findViews(){

        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        appImageView = findViewById(R.id.appImage);
        descriptionView = findViewById(R.id.descriptionView);
        companyView = findViewById(R.id.companyView);
        scoreBar = findViewById(R.id.scoreBar);
    }

    private String appName;
    private String imageURL;
    private String description;
    private String company;
    private String score;

    public void getExtras(){

        Intent parentIntent = getIntent();

        appName = parentIntent.getStringExtra("appName");
        imageURL = parentIntent.getStringExtra("imageURL");
        description = parentIntent.getStringExtra("description");
        company = parentIntent.getStringExtra("company");
        score = parentIntent.getStringExtra("score");
    }

    public void updateDetails(){

        Picasso.with(getApplicationContext())
                .load(imageURL)
                .fit()
                .into(appImageView);

        toolbar.setTitle(appName);
        descriptionView.setText(description);
        companyView.setText(company);
        scoreBar.setRating(Float.valueOf(score));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

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

        FloatingActionButton downloadAppFab = findViewById(R.id.fab);
        downloadAppFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Download", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
