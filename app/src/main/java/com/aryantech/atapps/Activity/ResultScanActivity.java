package com.aryantech.atapps.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aryantech.atapps.Activity.Camera.MRZStillImageDetectionActivity;
import com.aryantech.atapps.Activity.Class.Passport;
import com.aryantech.atapps.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.scanbot.mrzscanner.model.MRZRecognitionResult;

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

        Log.d("1",result.optional1);
        Log.d("2",result.optional2);
        Log.d("3",result.documentCode);
        Log.d("4",result.travelDocType.name());

        return intent;
    }
    Bitmap face;
    Bitmap facePassport;
    Bitmap passport;
    ImageView imageView_face,imageView_passport;
    EditText editText_firstName,editText_surName,editText_passportNo,editText_gender,editText_issuingCountry,
            editText_nationality,editText_dob,editText_exDate,editText_myKad,editText_issuingDate,editText_placeBirth;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_scan);


        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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

        linear_placeBirth = findViewById(R.id.linear_placeBirth);
        linear_myKad = findViewById(R.id.linear_myKad);
        button_save = findViewById(R.id.button_save);

        detectFaceGALERY(MRZStillImageDetectionActivity.getImage());
        imageView_passport.setImageBitmap(MRZStillImageDetectionActivity.getImage());
        passport = MRZStillImageDetectionActivity.getImage();

        editText_firstName.setText(getIntent().getStringExtra(EXTRA_firstName));
        editText_surName.setText(getIntent().getStringExtra(EXTRA_lastName));
        editText_passportNo.setText(getIntent().getStringExtra(EXTRA_documentCode));
        editText_gender.setText(getIntent().getStringExtra(EXTRA_gender));
        editText_issuingCountry.setText(getIntent().getStringExtra(EXTRA_issuingStateOrOrganization));
        editText_nationality.setText(getIntent().getStringExtra(EXTRA_nationality));
        editText_dob.setText(getIntent().getStringExtra(EXTRA_dateOfBirth));
        editText_exDate.setText(getIntent().getStringExtra(EXTRA_dateOfExpiry));

        storageLocationFace = getIntent().getStringExtra(EXTRA_documentCode) + "/";

        storagePassport = getIntent().getStringExtra(EXTRA_documentCode) + "/ Passport" + "/" ;

        if(getIntent().getStringExtra(EXTRA_nationality).equals("MYS")){
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
            linear_placeBirth.setVisibility(View.GONE);
            linear_myKad.setVisibility(View.GONE);
        }

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                standardProgressDialog.show();
                saveFirebase();
            }
        });
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
                    editText_myKad.getText().toString(),
                    editText_placeBirth.getText().toString(),
                    photoStringLink,
                    passportStringLink,
                    formattedDate);
            databaseReference.child(id).setValue(passport);
            Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show();
            standardProgressDialog.dismiss();

            Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
            startActivity(next);
    }

}
