package com.aryantech.atapps.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    List<Passport> passport;
    DatabaseReference databaseReference;
    StandardProgressDialog standardProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());
        passport = new ArrayList<>();
        listView = findViewById(R.id.listView);
        databaseReference = FirebaseDatabase.getInstance().getReference("Passport");
    }

    @Override
    protected void onStart() {
        super.onStart();
        standardProgressDialog.show();
        Long tsLong = System.currentTimeMillis()/1000;
        String formattedDate = tsLong.toString();

        Query query = databaseReference.orderByChild("dateScan");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passport.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Passport passports = postSnapshot.getValue(Passport.class);
                    passport.add(passports);
                }
                Collections.reverse(passport);
                HistoryList historyList = new HistoryList(HistoryActivity.this, passport);
                listView.setAdapter(historyList);
                standardProgressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
