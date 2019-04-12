package com.aryantech.atapps.Activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryList extends ArrayAdapter<Passport> {
    private Activity context;
    List<Passport> passport;

    public HistoryList(Activity context, List<Passport> passport) {
        super(context, R.layout.layout_history_list, passport);
        this.context = context;
        this.passport = passport;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_history_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewRating = (TextView) listViewItem.findViewById(R.id.passportNo);
        ImageView imageView_face = listViewItem.findViewById(R.id.imageView_face);

        Passport passports = passport.get(position);
        textViewName.setText(passports.getFirstName());
        textViewRating.setText(passports.getPassportNo());
        Picasso.get().load(passports.getFaceURL()).into(imageView_face);


        return listViewItem;
    }
}