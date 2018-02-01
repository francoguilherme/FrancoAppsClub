package com.guilherme.appsclub;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter{

    private Context context;
    private int layoutResourceID;
    private ArrayList appsList = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceID, ArrayList appsList) {

        super(context, layoutResourceID, appsList);

        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.appsList = appsList;
    }

    // Creates a new view for each app in the grid
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder;

        if (row == null){

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);
            holder = new ViewHolder();
            holder.appName = row.findViewById(R.id.text);
            holder.image = row.findViewById(R.id.image);
            row.setTag(holder);

        } else{

            holder = (ViewHolder) row.getTag();
        }

        AppItem item = (AppItem) appsList.get(position);
        holder.appName.setText(item.getAppName());
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    static class ViewHolder{

        TextView appName;
        ImageView image;
    }
}
