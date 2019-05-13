package com.aryantech.atapps.Activity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.R;

import java.io.File;
import java.util.ArrayList;

class HistoryList extends ArrayAdapter<Passport> implements View.OnClickListener{

    private ArrayList<Passport> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView textViewName;
        TextView passportNo;
        ImageView imageView_face;
    }

    public HistoryList(ArrayList<Passport> data, Context context) {
        super(context, R.layout.layout_history_list, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Passport dataModel=(Passport) object;


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Passport dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_history_list, parent, false);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.passportNo = (TextView) convertView.findViewById(R.id.passportNo);
            viewHolder.imageView_face = convertView.findViewById(R.id.imageView_face);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        viewHolder.textViewName.setText(dataModel.getFirstName()+" "+dataModel.getSecondName());
        viewHolder.passportNo.setText(dataModel.getPassportNo());


        File imgFile = new  File(dataModel.getPassportURL());

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            viewHolder.imageView_face.setImageBitmap(myBitmap);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
