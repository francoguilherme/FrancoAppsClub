package com.guilherme.appsclub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter{

    private Context context;
    private int gridLayoutResourceID;
    private ArrayList appsList = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceID, ArrayList appsList) {

        super(context, layoutResourceID, appsList);

        this.context = context;
        this.gridLayoutResourceID = layoutResourceID;
        this.appsList = appsList;
    }

    static class ViewHolder{

        TextView appName;
        ImageView image;
    }

    // Creates a new view for each app in the grid
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null){

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(gridLayoutResourceID, parent, false);
            holder = new ViewHolder();
            holder.appName = row.findViewById(R.id.appName);
            holder.image = row.findViewById(R.id.appImage);
            row.setTag(holder);

        } else{

            holder = (ViewHolder) row.getTag();
        }

        AppItem appItem = (AppItem) appsList.get(position);
        // Gets the image URL from the list of apps and downloads it
        Picasso.with(getContext())
                .load(appItem.getImageURL())
                .fit()
                .into(holder.image);

        holder.appName.setText(appItem.getAppName());
        return row;
    }
}
