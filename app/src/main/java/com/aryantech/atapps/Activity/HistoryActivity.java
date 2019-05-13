package com.aryantech.atapps.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.Activity.Class.PassportDB;
import com.aryantech.atapps.R;
import com.google.android.gms.vision.L;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Passport> passports;
    DatabaseReference databaseReference;
    StandardProgressDialog standardProgressDialog;
    private ArrayList<String> inventoryItems;
    private ArrayAdapter inventoryItemsAdapter;
    LinearLayout linear_not,linear_done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        listView = findViewById(R.id.listView);
        linear_not = findViewById(R.id.linear_not);
        linear_done = findViewById(R.id.linear_done);


        linear_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInventoryListNotPost();
            }
        });

        linear_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInventoryListDonePost();
            }
        });
    }

    private List<PassportDB> getAll() {
        //Getting all items stored in Inventory table
        return new Select()
                .from(PassportDB.class)
                .orderBy("scan_date DESC")
                .execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        standardProgressDialog.show();
        showInventoryListNotPost();

    }

    private void showInventoryListNotPost() {
        standardProgressDialog.dismiss();
        passports =  new ArrayList<>();
        List<PassportDB> inventories = getAll();
        for (int i = 0; i < inventories.size(); i++) {
            PassportDB inventory = inventories.get(i);

            Log.d("inventory.uploadStatus",inventory.uploadStatus);

            if(inventory.uploadStatus.equals("0")){
                passports.add(new Passport(
                        "0",
                        inventory.first_name,
                        inventory.last_name,
                        inventory.pass_no,
                        inventory.gender,
                        inventory.issuing_off,
                        inventory.citizenship,
                        inventory.birth_date,
                        inventory.expiry_date,
                        inventory.issuing_date,
                        inventory.no_phone,
                        inventory.issuing_off,
                        inventory.ic_no,
                        inventory.birth_place,
                        "1",
                        inventory.pas_img,
                        String.valueOf(inventory.scan_date),
                        inventory.uploadStatus
                ));
            }
        }

        inventoryItemsAdapter= new HistoryList(passports,getApplicationContext());
        listView.setAdapter(inventoryItemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Passport dataModel= passports.get(position);

                Intent next = new Intent(getApplicationContext(),EditActivity.class);
                next.putExtra("a",dataModel.getId());
                next.putExtra("b",dataModel.getFirstName());
                next.putExtra("cc",dataModel.getSecondName());
                next.putExtra("d",dataModel.getPassportNo());
                next.putExtra("e",dataModel.getGender());
                next.putExtra("f",dataModel.getIssue_country());
                next.putExtra("g",dataModel.getNationality());
                next.putExtra("h",dataModel.getDob());
                next.putExtra("i",dataModel.getDoe());
                next.putExtra("j",dataModel.getDoi());
                next.putExtra("k",dataModel.getPhone_no());
                next.putExtra("l",dataModel.getIssue_place());
                next.putExtra("m",dataModel.getMyKad());
                next.putExtra("n",dataModel.getState_dob());
                next.putExtra("o",dataModel.getFaceURL());
                next.putExtra("p",dataModel.getPassportURL());
                next.putExtra("q",dataModel.getDateScan());
                next.putExtra("r",dataModel.getStatusScan());

                startActivity(next);
            }
        });


    }

    private void showInventoryListDonePost() {
        standardProgressDialog.dismiss();
        passports =  new ArrayList<>();
        List<PassportDB> inventories = getAll();
        for (int i = 0; i < inventories.size(); i++) {
            PassportDB inventory = inventories.get(i);

            if(inventory.uploadStatus.equals("1")){
                passports.add(new Passport(
                        "1",
                        inventory.first_name,
                        inventory.last_name,
                        inventory.pass_no,
                        inventory.gender,
                        inventory.issuing_off,
                        inventory.citizenship,
                        inventory.birth_date,
                        inventory.expiry_date,
                        inventory.issuing_date,
                        inventory.no_phone,
                        inventory.issuing_off,
                        inventory.ic_no,
                        inventory.birth_place,
                        "1",
                        inventory.pas_img,
                        String.valueOf(inventory.scan_date),
                        inventory.uploadStatus
                ));
            }

        }

        inventoryItemsAdapter= new HistoryList(passports,getApplicationContext());
        listView.setAdapter(inventoryItemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }



}
