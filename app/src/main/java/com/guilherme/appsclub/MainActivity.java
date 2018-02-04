package com.guilherme.appsclub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public GridView gridView;
    public GridViewAdapter gridViewAdapter;

    final String AVAILABLE_COUNTRIES = "br, us, uk";
    String countryCode = "";

    boolean isInternetOn(){
        //Check if connected to internet
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void createGridView(ArrayList<AppItem> apps){

        gridView = findViewById(R.id.gridView);

        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, apps);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppItem item = (AppItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("appName", item.getAppName());
                intent.putExtra("image", item.getImage());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("company", item.getCompany());
                intent.putExtra("score", item.getScore());

                final ImageView appImage = view.findViewById(R.id.appImage);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (MainActivity.this, appImage, "appImage");

                startActivity(intent, options.toBundle());
            }
        });
    }

    private void findCountryCode(){

        // Finds the user's ISO country code
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

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findCountryCode();

        if (!AVAILABLE_COUNTRIES.contains(countryCode)){

            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Não há aplicativos disponíveis para seu país",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            return;
        }

        final MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        model.setCountryCode(countryCode);

        if (isInternetOn() && model.appItems.isEmpty()){

            model.getApps().observe(this, new Observer<ArrayList<AppItem>>() {
                @Override
                public void onChanged(@Nullable ArrayList<AppItem> appItems) {

                    progressBar.setVisibility(View.GONE);

                    createGridView(appItems);

                    for (int i = 0; i < model.imageURLs.size(); i++){

                        Picasso.with(getApplicationContext()).load(model.imageURLs.get(i)).into((ImageView) gridView.getChildAt(i).getTag(1));
                    }
                }
            });

        } else if (model.appItems.isEmpty()){

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
            createGridView(model.appItems);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_br) {

        } else if (id == R.id.nav_us) {

        } else if (id == R.id.nav_uk) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
