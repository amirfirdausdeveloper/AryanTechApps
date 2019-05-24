package com.aryantech.atapps.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aryantech.atapps.Activity.Camera.MRZLiveDetectionActivity;
import com.aryantech.atapps.Activity.Camera.MRZStillImageDetectionActivity;
import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.Activity.Class.PassportDB;
import com.aryantech.atapps.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.L;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.scanbot.mrzscanner.model.MRZRecognitionResult;

import static com.android.volley.Request.Method.POST;

public class ResultScanActivity extends AppCompatActivity {

    public static final String EXTRA_documentCode = "documentCode";
    public static final String EXTRA_firstName = "firstName";
    public static final String EXTRA_lastName = "lastName";
    public static final String EXTRA_issuingStateOrOrganization = "issuingStateOrOrganization";
    public static final String EXTRA_departmentOfIssuance = "departmentOfIssuance";
    public static final String EXTRA_nationality = "nationality";
    public static final String EXTRA_dateOfBirth = "dateOfBirth";
    public static final String EXTRA_gender = "gender";
    public static final String EXTRA_dateOfExpiry = "dateOfExpiry";
    public static final String EXTRA_personalNumber = "personalNumber";
    public static final String EXTRA_travelDocTyp = "travelDocType";

    public static Intent newIntent(Context context, MRZRecognitionResult result) {
        Intent intent = new Intent(context, ResultScanActivity.class);
        intent.putExtra(EXTRA_documentCode, result.documentCode);
        intent.putExtra(EXTRA_firstName, result.firstName);
        intent.putExtra(EXTRA_lastName, result.lastName);
        intent.putExtra(EXTRA_issuingStateOrOrganization, result.issuingStateOrOrganization);
        intent.putExtra(EXTRA_nationality, result.nationality);
        intent.putExtra(EXTRA_dateOfBirth, result.dateOfBirth);
        intent.putExtra(EXTRA_gender, result.gender);
        intent.putExtra(EXTRA_dateOfExpiry, result.dateOfExpiry);
        intent.putExtra(EXTRA_personalNumber, result.personalNumber);
        intent.putExtra(EXTRA_travelDocTyp, result.travelDocType.name());

        Log.d("1",result.issuingStateOrOrganization);
        Log.d("2",result.departmentOfIssuance);
        Log.d("3",result.discreetIssuingStateOrOrganization);

        return intent;
    }
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_scan);


        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        formattedDate = df.format(c);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
        } else {
            signInAnonymously();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Passport");
        storageReference = FirebaseStorage.getInstance().getReference();
//        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        imageView_face = findViewById(R.id.imageView_face);
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

        detectFaceGALERY(MRZLiveDetectionActivity.getImage());
        imageView_passport.setImageBitmap(MRZLiveDetectionActivity.getImage());
        passport = MRZLiveDetectionActivity.getImage();


        editText_passportNo.setText(getIntent().getStringExtra(EXTRA_documentCode));
        editText_gender.setText(getIntent().getStringExtra(EXTRA_gender));
        editText_issuingCountry.setText(getIntent().getStringExtra(EXTRA_issuingStateOrOrganization));
        editText_nationality.setText(getIntent().getStringExtra(EXTRA_nationality));
        editText_dob.setText(getIntent().getStringExtra(EXTRA_dateOfBirth));
        editText_exDate.setText(getIntent().getStringExtra(EXTRA_dateOfExpiry));

        storageLocationFace = getIntent().getStringExtra(EXTRA_documentCode) + "/";

        storagePassport = getIntent().getStringExtra(EXTRA_documentCode) + "/ Passport" + "/" ;

        if(getIntent().getStringExtra(EXTRA_nationality).equals("MYS")){

            editText_firstName.setText(getIntent().getStringExtra(EXTRA_lastName)+" "+getIntent().getStringExtra(EXTRA_firstName));
            editText_surName.setText("");

            linear_placeBirth.setVisibility(View.VISIBLE);
            linear_myKad.setVisibility(View.VISIBLE);
            editText_myKad.setText(getIntent().getStringExtra(EXTRA_personalNumber));

            ic_no = getIntent().getStringExtra(EXTRA_personalNumber);

            String substringIC = ic_no.substring(6,8);
            Log.d("substringIC",substringIC);

            if(substringIC.equals("01")){
                editText_placeBirth.setText("JOHOR");
            }else if(substringIC.equals("02")){
                editText_placeBirth.setText("KEDAH");
            }else if(substringIC.equals("03")){
                editText_placeBirth.setText("KELANTAN");
            }else if(substringIC.equals("04")){
                editText_placeBirth.setText("MELAKA");
            }else if(substringIC.equals("05")){
                editText_placeBirth.setText("NEGERI SEMBILAN");
            }else if(substringIC.equals("06")){
                editText_placeBirth.setText("PAHANG");
            }else if(substringIC.equals("07")){
                editText_placeBirth.setText("PULAU PINANG");
            }else if(substringIC.equals("08")){
                editText_placeBirth.setText("PERAK");
            }else if(substringIC.equals("09")){
                editText_placeBirth.setText("PERLIS");
            }else if(substringIC.equals("10")){
                editText_placeBirth.setText("SELANGOR");
            }else if(substringIC.equals("11")){
                editText_placeBirth.setText("TERENGGANU");
            }else if(substringIC.equals("12")){
                editText_placeBirth.setText("SABAH");
            }else if(substringIC.equals("13")){
                editText_placeBirth.setText("SARAWAK");
            }else if(substringIC.equals("14")){
                editText_placeBirth.setText("KUALA LUMPUR");
            }
        }else{
            editText_firstName.setText(getIntent().getStringExtra(EXTRA_firstName));
            editText_surName.setText(getIntent().getStringExtra(EXTRA_lastName));


            linear_placeBirth.setVisibility(View.GONE);
            linear_myKad.setVisibility(View.GONE);
        }

        if(getIntent().getStringExtra(EXTRA_nationality).equals("IDN")){
            editText_firstName.setText(getIntent().getStringExtra(EXTRA_lastName)+" "+getIntent().getStringExtra(EXTRA_firstName));
            editText_surName.setText("");
        }

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standardProgressDialog.show();

                if(editText_issuingDate.getText().toString().equals("")){
                    standardProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please insert ISSUING DATE",Toast.LENGTH_SHORT).show();
                }else if(editText_phone.getText().toString().equals("")){
                    standardProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please insert phone no",Toast.LENGTH_SHORT).show();
                }else if(editText_issuePlace.getText().toString().equals("")){
                    standardProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please insert issue place",Toast.LENGTH_SHORT).show();
                }else{
                    saveToDatabase();
                }

            }
        });

        TextWatcher tw = ValidationBirthday();
        editText_issuingDate.addTextChangedListener(tw);

        button_save2 = findViewById(R.id.button_save2);
        button_save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = true;
                List<PassportDB> inventories = getAll();
                for (int i = 0; i < inventories.size(); i++) {
                    PassportDB inventory = inventories.get(i);
                       if(inventory.pass_no.equals(editText_passportNo.getText().toString())){
                           Log.d("ada","ada");
                           status = false;
                           break;
                       }else{
                           status = true;
                         Log.d("xda","xda");
                         break;
                       }
                }
//
                if(status == true){
                    SaveImage(MRZLiveDetectionActivity.getImage());
                    PassportDB pp = new PassportDB();
                    pp.first_name = editText_firstName.getText().toString();
                    pp.last_name = editText_surName.getText().toString();
                    pp.pass_no = editText_passportNo.getText().toString();
                    pp.gender = editText_gender.getText().toString();
                    pp.country_code = editText_issuingCountry.getText().toString();
                    pp.citizenship = editText_nationality.getText().toString();
                    pp.birth_date = editText_dob.getText().toString();
                    pp.expiry_date = editText_exDate.getText().toString();
                    pp.issuing_date = editText_issuingDate.getText().toString();
                    pp.no_phone = editText_phone.getText().toString();
                    pp.issuing_off = editText_issuePlace.getText().toString();
                    pp.ic_no = editText_myKad.getText().toString();
                    pp.birth_place = editText_placeBirth.getText().toString();
                    pp.scan_date = c;
                    pp.pas_img = url_images_local;
                    pp.type = "Passport";
                    pp.uploadStatus = "0";
                    pp.save();

                    Toast.makeText(getApplicationContext(),"Save local success",Toast.LENGTH_LONG).show();
                    Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                    startActivity(next);
                }else {
                    Toast.makeText(getApplicationContext(),"Record already exist",Toast.LENGTH_LONG).show();
                }




            }
        });

        getAll();

        editText_firstName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_surName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_passportNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        editText_gender.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_issuingCountry.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_nationality.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        editText_issuePlace.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        editText_placeBirth.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }

    private List<PassportDB> getAll() {
        //Getting all items stored in Inventory table
        return new Select()
                .from(PassportDB.class)
                .orderBy("scan_date DESC")
                .execute();
    }


    @NonNull
    private TextWatcher ValidationBirthday() {
        return new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");
                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;
                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));
                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 01) ? 01 : (year > 99) ? 99 : year;
                        cal.set(Calendar.YEAR, year);
                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;


                        clean = String.format("%02d%02d%02d", day, mon, year);
                        dayss = String.valueOf(day);
                        monthss = String.valueOf(mon);
                        yearss = String.valueOf(year);
                    }
                    clean = String.format("%s.%s.%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 6));
                    sel = sel < 0 ? 0 : sel;
                    current = clean;


                    editText_issuingDate.setText(current);
                    editText_issuingDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void detectFaceGALERY(Bitmap bitmap) {
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        if (detector.isOperational() && bitmap != null) {
            face = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
            Canvas canvas = new Canvas(face);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(face).build();
            SparseArray<Face> faces = detector.detect(frame);

            Bitmap bmOverlay = null;

            for (int index = 0; index < 1; ++index) {
                Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);

                Matrix mat = new Matrix();
                int x = (int) face.getPosition().x;
                int y = (int) face.getPosition().y;
                int width = (int) face.getWidth() / 2;
                int height = (int) face.getHeight() / 2;

                bmOverlay = Bitmap.createBitmap(bitmap, x  , y,width +width,height + height,mat,true);

            }

            if (faces.size() == 0) {


            } else {
                imageView_face.setImageBitmap(bmOverlay);
                facePassport = bmOverlay;

            }
        } else {

        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.e("MainActivity", "sucess****** ");

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("MainActivity", "signFailed****** ", exception);
            }
        });
    }

    private void saveFirebase() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        facePassport.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoStringLink = uri.toString();
                        savePassport();
                    }
                });

            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };

        uploadFirebase(editText_passportNo.getText().toString(),data, successListener, failureListener);
    }

    private void savePassport() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        passport.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        passportStringLink = uri.toString();
                        UploadImageFileToFirebaseStorage();
                    }
                });

            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };

        uploadFirebasePassport(editText_passportNo.getText().toString(),data, successListener, failureListener);
    }

    private void uploadFirebase(String filename, byte[] data, OnSuccessListener<UploadTask.TaskSnapshot> successListener, OnFailureListener failureListener){
        final StorageReference ref = storageReference.child(storageLocationFace +  filename + ".jpg");
        ref.putBytes(data).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    private void uploadFirebasePassport(String filename, byte[] data, OnSuccessListener<UploadTask.TaskSnapshot> successListener, OnFailureListener failureListener){
        final StorageReference ref = storageReference.child(storagePassport +  filename + ".jpg");
        ref.putBytes(data).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void UploadImageFileToFirebaseStorage() {

            String id = databaseReference.push().getKey();
            Passport passport = new Passport(
                    id,
                    getIntent().getStringExtra(EXTRA_firstName),
                    getIntent().getStringExtra(EXTRA_lastName),
                    editText_passportNo.getText().toString(),
                    editText_gender.getText().toString(),
                    editText_issuingCountry.getText().toString(),
                    editText_nationality.getText().toString(),
                    editText_dob.getText().toString(),
                    editText_exDate.getText().toString(),
                    editText_issuingDate.getText().toString(),
                    editText_phone.getText().toString(),
                    editText_issuePlace.getText().toString(),
                    editText_myKad.getText().toString(),
                    editText_placeBirth.getText().toString(),
                    photoStringLink,
                    passportStringLink,
                    formattedDate,
                    "1");
            databaseReference.child(id).setValue(passport);
            Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show();
            standardProgressDialog.dismiss();

            Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
            startActivity(next);



    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            url_images_local = Environment.getExternalStorageDirectory()+ "/saved_images/"+fname;
            Log.d("FILE",url_images_local);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
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
            data.put("pas_img",getStringImage(MRZLiveDetectionActivity.getImage()));
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
                                SaveImage(MRZLiveDetectionActivity.getImage());
                                PassportDB pp = new PassportDB();
                                pp.first_name = editText_firstName.getText().toString();
                                pp.last_name = editText_surName.getText().toString();
                                pp.pass_no = editText_passportNo.getText().toString();
                                pp.gender = editText_gender.getText().toString();
                                pp.country_code = editText_issuingCountry.getText().toString();
                                pp.citizenship = editText_nationality.getText().toString();
                                pp.birth_date = editText_dob.getText().toString();
                                pp.expiry_date = editText_exDate.getText().toString();
                                pp.issuing_date = editText_issuingDate.getText().toString();
                                pp.no_phone = editText_phone.getText().toString();
                                pp.issuing_off = editText_issuePlace.getText().toString();
                                pp.ic_no = editText_myKad.getText().toString();
                                pp.birth_place = editText_placeBirth.getText().toString();
                                pp.scan_date = c;
                                pp.pas_img = url_images_local;
                                pp.type = "Passport";
                                pp.uploadStatus = "1";
                                pp.save();

                                Toast.makeText(getApplicationContext(),"Save to server success",Toast.LENGTH_LONG).show();
                                Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                                startActivity(next);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_LONG).show();
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

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Log.d("ERROR",data.toString());
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

}
