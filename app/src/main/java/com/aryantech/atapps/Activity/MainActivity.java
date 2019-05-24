package com.aryantech.atapps.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.aryantech.atapps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.doo.snap.ScanbotSDK;
import net.doo.snap.blob.BlobFactory;
import net.doo.snap.blob.BlobManager;
import net.doo.snap.entity.Blob;
import net.doo.snap.entity.Language;
import net.doo.snap.util.log.Logger;
import net.doo.snap.util.log.LoggerProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MyActivity";
    Handler handler;

    private final Logger logger = LoggerProvider.getLogger();
    private Uri file;
    private ScanbotSDK scanbotSDK;
    private BlobManager blobManager;
    private BlobFactory blobFactory;
    private FirebaseAuth mAuth;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path  = Environment.getExternalStorageDirectory()+"/Android/data/com.aryantech.atapps/files/binaries/tessdata/";

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"- onResume()");

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(next);
            }
        }, 2500);



//        copyFile(getApplicationContext());
        initDependencies();
        downloadMRZTraineddata();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                File deleted = new File(file, "ocrb.traineddata");
                if (!file.exists()) {
                    Log.d("EXIST","EXIST");
                }else{
                    boolean d0 = deleted.delete();
                    Log.w("Delete Check", "File deleted: " + file + d0);
                    copyFile(getApplicationContext());
                    Log.d("NOT","EXIST");
                }

            }
        }, 2000);



    }

    private void copyFile(Context context) {
        File file = new File(path);


        AssetManager assetManager = context.getAssets();
        try {
            InputStream in = assetManager.open("ocrb.traineddata");
            OutputStream out = new FileOutputStream(path+"ocrb.traineddata");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"- onStop()");

        handler.removeCallbacksAndMessages(null);
    }

    private void initDependencies() {
        scanbotSDK = new ScanbotSDK(this);
        blobManager = scanbotSDK.blobManager();
        blobFactory = scanbotSDK.blobFactory();
    }

//    private void copyFile(Context context) {
//
//        AssetManager assetManager = context.getAssets();
//        try {
//            InputStream in = assetManager.open("ocrb.traineddata");
//            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/yourapp/ocrb.traineddata");
//            byte[] buffer = new byte[1024];
//            int read = in.read(buffer);
//            while (read != -1) {
//                out.write(buffer, 0, read);
//                read = in.read(buffer);
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }

    private void downloadMRZTraineddata() {
        try {
            final Blob mrzBlob = blobFactory.mrzTraineddataBlob();
            if (!blobManager.isBlobAvailable(mrzBlob)) {
                new DownloadOCRDataTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                return;
            }
        } catch (IOException e) {
            logger.logException(e);
        }
    }

    private class DownloadOCRDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                blobManager.fetch(blobFactory.mrzTraineddataBlob(), false);
            } catch (IOException e) {
                logger.logException(e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
