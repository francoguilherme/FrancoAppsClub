package com.guilherme.appsclub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    boolean isInternetOn(){

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    public void createGridView(ArrayList<AppItem> appsList){
        // Creates the grid used to display the apps

        gridView = findViewById(R.id.gridView);

        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, appsList);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppItem appItem = (AppItem) parent.getItemAtPosition(position);

                // Creates an intent to change from the list of apps to the details of an app
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("appName", appItem.getAppName());
                detailsIntent.putExtra("imageURL", appItem.getImageURL());
                detailsIntent.putExtra("description", appItem.getDescription());
                detailsIntent.putExtra("company", appItem.getCompany());
                detailsIntent.putExtra("score", appItem.getScore());

                final ImageView appImage = view.findViewById(R.id.appImage);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (MainActivity.this, appImage, "appImage");

                // Changes to the DetailsActivity animating the app icon across screens
                startActivity(detailsIntent, options.toBundle());
            }
        });
    }

    String countryCode = "";

    private void findCountryCode(){
        // Country code in ISO standards

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = telephonyManager.getNetworkCountryIso();

        if (!countryCode.isEmpty()){
            return; // If code was already found, move on
        }

        // If not, find country code through the user's IP
        FindCountryTask findCountryTask = new FindCountryTask();
        try {
            countryCode = findCountryTask.execute("http://ip-api.com/json").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is done early so we don't download anything that won't be shown to the user
        findCountryCode();

        final String availableCountries = "br, us, gb";

        if (!availableCountries.contains(countryCode)){

            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Não há aplicativos disponíveis para seu país",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            return;
        }

        // This view model will hold every app information we need to download
        final AppsInfoViewModel appsInfoModel = ViewModelProviders.of(this).get(AppsInfoViewModel.class);

        // Sets the country code so the ViewModel knows from which country to download
        appsInfoModel.setCountryCode(countryCode);

        if (isInternetOn() && appsInfoModel.appItems.isEmpty()){
            // Nothing has been downloaded yet

            final ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            appsInfoModel.getAppsData().observe(this, new Observer<ArrayList<AppItem>>() {
                @Override
                public void onChanged(@Nullable ArrayList<AppItem> appItems) {
                    // Ran when it finishes downloading

                    progressBar.setVisibility(View.GONE);
                    createGridView(appItems);
                }
            });

        } else if (appsInfoModel.appItems.isEmpty()){

            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Sem conexão com a internet.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Tentar de novo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.this.recreate();

                }
            }).show();

        } else {

            //  This is only called when there is a configuration change and the app
            //  has already downloaded stuff, so it just needs to display it again
            createGridView(appsInfoModel.appItems);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
