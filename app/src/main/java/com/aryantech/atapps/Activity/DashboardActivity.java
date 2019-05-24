package com.aryantech.atapps.Activity;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.aryantech.atapps.Activity.Camera.MRZLiveDetectionActivity;
import com.aryantech.atapps.Activity.Camera.MRZStillImageDetectionActivity;
import com.aryantech.atapps.Activity.Class.PassportDB;
import com.aryantech.atapps.R;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    LinearLayout linear_passport,linear_history;
    private static long back_pressed;
    int totals = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActivityCompat.requestPermissions(DashboardActivity.this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                1);


        linear_passport = findViewById(R.id.linear_passport);
        linear_history = findViewById(R.id.linear_history);
    }

    @Override
    protected void onResume() {
        super.onResume();

        linear_passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totals > 50){
                    Toast.makeText(getApplicationContext(),"Please connect to internet and save data to database in history or delete history send to server",Toast.LENGTH_LONG).show();
                }else{
                    Intent next = new Intent(getApplicationContext(), MRZLiveDetectionActivity.class);
                    startActivity(next);
                }

            }
        });

        linear_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(next);
            }
        });

        getAll();
        getTotal();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())  moveTaskToBack(true);
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    private List<PassportDB> getAll() {
        //Getting all items stored in Inventory table
        return new Select()
                .from(PassportDB.class)
                .orderBy("scan_date DESC")
                .execute();
    }

    private void getTotal(){
        List<PassportDB> inventories = getAll();
        for (int i = 0; i < inventories.size(); i++) {
            PassportDB inventory = inventories.get(i);
            if(inventory.uploadStatus.equals("0")){
                totals++;
            }
        }
    }
}
