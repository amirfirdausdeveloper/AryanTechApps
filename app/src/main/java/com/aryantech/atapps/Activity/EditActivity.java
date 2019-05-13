package com.aryantech.atapps.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aryantech.atapps.Activity.Camera.MRZStillImageDetectionActivity;
import com.aryantech.atapps.Activity.Class.PassportDB;
import com.aryantech.atapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    Bitmap face;
    Bitmap facePassport;
    Bitmap passport;
    ImageView imageView_face,imageView_passport;
    EditText editText_firstName,editText_surName,editText_passportNo,editText_gender,editText_issuingCountry,
            editText_nationality,editText_dob,editText_exDate,editText_myKad,editText_issuingDate,editText_placeBirth,editText_phone,editText_issuePlace;

    LinearLayout linear_placeBirth,linear_myKad;
    String ic_no = "";

    StorageReference storageReference;
    DatabaseReference databaseReference;
    Button button_save;
    String photoStringLink = "",passportStringLink="";
    private Uri fbFace;
    private Uri fbPassport;
    String storageLocationFace = "",storagePassport = "";
    private FirebaseAuth mAuth;
    String formattedDate = "";
    StandardProgressDialog standardProgressDialog;
    String days = "";
    String months = "";
    String years = "";
    String dayss = "";
    String monthss = "";
    String yearss = "";
    Button button_save2;
    SimpleDateFormat df;
    Date dates,c;
    String url_images_local = "";
    Bitmap myBitmap;

    String a,b,cc,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        a = getIntent().getStringExtra("a");
        b = getIntent().getStringExtra("b");
        cc = getIntent().getStringExtra("cc");
        d = getIntent().getStringExtra("d");
        e = getIntent().getStringExtra("e");
        f = getIntent().getStringExtra("f");
        g = getIntent().getStringExtra("g");
        h = getIntent().getStringExtra("h");
        i = getIntent().getStringExtra("i");
        j = getIntent().getStringExtra("j");
        k = getIntent().getStringExtra("k");
        l = getIntent().getStringExtra("l");
        m = getIntent().getStringExtra("m");
        n = getIntent().getStringExtra("n");
        o = getIntent().getStringExtra("o");
        p = getIntent().getStringExtra("p");
        q = getIntent().getStringExtra("q");
        r = getIntent().getStringExtra("r");

        imageView_passport = findViewById(R.id.imageView_passport);

        editText_firstName = findViewById(R.id.editText_firstName);
        editText_surName = findViewById(R.id.editText_surName);
        editText_passportNo = findViewById(R.id.editText_passportNo);
        editText_gender = findViewById(R.id.editText_gender);
        editText_issuingCountry = findViewById(R.id.editText_issuingCountry);
        editText_nationality = findViewById(R.id.editText_nationality);
        editText_dob = findViewById(R.id.editText_dob);
        editText_exDate = findViewById(R.id.editText_exDate);
        editText_myKad = findViewById(R.id.editText_myKad);
        editText_issuingDate = findViewById(R.id.editText_issuingDate);
        editText_placeBirth = findViewById(R.id.editText_placeBirth);
        editText_phone = findViewById(R.id.editText_phone);
        editText_issuePlace = findViewById(R.id.editText_issuePlace);

        linear_placeBirth = findViewById(R.id.linear_placeBirth);
        linear_myKad = findViewById(R.id.linear_myKad);
        button_save = findViewById(R.id.button_save);


        editText_firstName.setText(b);
        editText_surName.setText(cc);
        editText_passportNo.setText(d);
        editText_gender.setText(e);
        editText_issuingCountry.setText(f);
        editText_nationality.setText(g);
        editText_dob.setText(h);
        editText_exDate.setText(i);
        editText_myKad.setText(m);
        editText_issuingDate.setText(j);
        editText_placeBirth.setText(n);
        editText_phone.setText(k);
        editText_issuePlace.setText(l);
        formattedDate = q;


        File imgFile = new  File(p);
        if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView_passport.setImageBitmap(myBitmap);
        }


        if(g.equals("MYS")) {
            linear_placeBirth.setVisibility(View.VISIBLE);
            linear_myKad.setVisibility(View.VISIBLE);
        }else{
            linear_placeBirth.setVisibility(View.GONE);
            linear_myKad.setVisibility(View.GONE);
        }



        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassportDB model = selectField("pass_no", editText_passportNo.getText().toString());
                model.uploadStatus = "1";
                model.save();

                standardProgressDialog.show();
                saveToDatabase();
            }
        });
    }

    public static PassportDB selectField(String fieldName, String fieldValue) {
        return new Select().from(PassportDB.class)
                .where(fieldName + " = ?", fieldValue).executeSingle();
    }

    private void saveToDatabase(){
        JSONObject data = new JSONObject();
        try {
            data.put("pass_no",editText_passportNo.getText().toString());
            data.put("type","P");
            data.put("expiry_date",editText_exDate.getText().toString());
            data.put("birth_date",editText_dob.getText().toString());
            data.put("issuing_date",editText_issuingDate.getText().toString());
            data.put("issuing_off",editText_issuePlace.getText().toString());
            data.put("birth_place",editText_placeBirth.getText().toString());
            if(editText_myKad.getText().toString().equals("")){
                data.put("ic_no","");
                data.put("first_name",editText_firstName.getText().toString());
                data.put("last_name",editText_surName.getText().toString());
            }else{
                data.put("ic_no",editText_myKad.getText().toString());
                data.put("first_name",editText_firstName.getText().toString()+" "+editText_surName.getText().toString());

            }
            data.put("gender",editText_gender.getText().toString());
            data.put("citizenship",editText_nationality.getText().toString());
            data.put("country_code",editText_issuingCountry.getText().toString());
            data.put("height","180");
            data.put("no_phone",editText_phone.getText().toString());
            data.put("scan_date",formattedDate);
            data.put("photo_filename",editText_passportNo.getText().toString());
            data.put("pas_img",getStringImage(myBitmap));
            data.put("agent_id","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://aryantech.asuscomm.com/cdic/save_db.php", data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("true")){
                                Toast.makeText(getApplicationContext(),"Save to server success",Toast.LENGTH_LONG).show();
                                Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                                startActivity(next);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public String getBodyContentType(){
                return "application/json";
            }
        };
        mQueue.add(jsonObjectRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }
}
