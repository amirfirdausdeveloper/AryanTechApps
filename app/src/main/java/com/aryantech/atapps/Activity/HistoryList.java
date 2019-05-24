package com.aryantech.atapps.Activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.Activity.Class.PassportDB;
import com.aryantech.atapps.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class HistoryList extends ArrayAdapter<Passport> implements View.OnClickListener{

    private ArrayList<Passport> dataSet;

    Context mContext;

    private static class ViewHolder {
        TextView textViewName;
        TextView passportNo;
        ImageView imageView_face;
        ImageView imageView_delete;
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
        final Passport dataModel = getItem(position);
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
            viewHolder.imageView_delete = convertView.findViewById(R.id.imageView_delete);
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

        viewHolder.imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = delete(dataModel.getPassportNo());
                diaBox.show();
            }
        });
        return convertView;
    }

    private AlertDialog delete(final String passportNo) {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(mContext)
                .setMessage("Are you sure want to delete this passport "+passportNo+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {
                        List<PassportDB> inventories = getDelete(passportNo);
                        Toast.makeText(mContext,"delete success",Toast.LENGTH_LONG).show();
                        Intent next = new Intent(mContext,DashboardActivity.class);
                        mContext.startActivity(next);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private List<PassportDB> getDelete(String passportNo) {
        return new Delete()
                .from(PassportDB.class)
                .where("pass_no =?", passportNo)
                .execute();
    }

}
