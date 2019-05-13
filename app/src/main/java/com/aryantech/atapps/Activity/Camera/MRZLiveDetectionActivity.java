package com.aryantech.atapps.Activity.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aryantech.atapps.Activity.DashboardActivity;
import com.aryantech.atapps.Activity.MyExceptionHandler;
import com.aryantech.atapps.Activity.ResultScanActivity;
import com.aryantech.atapps.R;

import net.doo.snap.ScanbotSDK;
import net.doo.snap.camera.CameraOpenCallback;
import net.doo.snap.camera.PictureCallback;
import net.doo.snap.camera.ScanbotCameraView;
import net.doo.snap.lib.detector.ContourDetector;
import net.doo.snap.mrzscanner.MRZScanner;
import net.doo.snap.mrzscanner.MRZScannerFrameHandler;
import net.doo.snap.util.log.Logger;
import net.doo.snap.util.log.LoggerProvider;

import io.scanbot.mrzscanner.model.MRZRecognitionResult;

public class MRZLiveDetectionActivity extends AppCompatActivity implements PictureCallback {

    private final Logger logger = LoggerProvider.getLogger();

    private ScanbotCameraView cameraView;
    private TextView resultView;
    Bitmap thumbnailImage;
    boolean flashEnabled = false;
    MRZScanner mrzScanner;
    public static Intent newIntent(Context context) {
        return new Intent(context, MRZLiveDetectionActivity.class);
    }

    private static Bitmap images;
    public void setImage(Bitmap images) {
        this.images = images;
    }
    public static Bitmap getImage() {
        return images;
    }
    Bitmap face;
    MRZRecognitionResult mrzRecognitionResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrz_live_scanner);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this)); // avoid crash


        ActivityCompat.requestPermissions(MRZLiveDetectionActivity.this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                1);



        cameraView = findViewById(R.id.camera);

        cameraView.setCameraOpenCallback(new CameraOpenCallback() {
            @Override
            public void onCameraOpened() {
                cameraView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cameraView.useFlash(flashEnabled);
                        cameraView.continuousFocus();
                    }
                }, 700);
            }
        });


        resultView = findViewById(R.id.result);
        cameraView.addPictureCallback(this);
        ScanbotSDK scanbotSDK = new ScanbotSDK(this);
        mrzScanner = scanbotSDK.mrzScanner();
        MRZScannerFrameHandler mrzScannerFrameHandler = MRZScannerFrameHandler.attach(cameraView, mrzScanner);

        mrzScannerFrameHandler.addResultHandler(new MRZScannerFrameHandler.ResultHandler() {
            @Override
            public boolean handleResult(final MRZRecognitionResult mrzRecognitionResult) {
                mrzRecognitionResults = mrzRecognitionResult;
                if (mrzRecognitionResult != null && mrzRecognitionResult.recognitionSuccessful) {
                    final long a = System.currentTimeMillis();

                    cameraView.takePicture(false);

//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                startActivity(ResultScanActivity.newIntent(MRZLiveDetectionActivity.this, mrzRecognitionResult));
//                            } finally {
//                                long b = System.currentTimeMillis();
//                                logger.d("MRZScanner", "Total scanning (sec): " + (b - a) / 1000f);
//                            }
//
//                        }
//                    }, 2000);



                }
                return false;
            }
        });






    }

    @Override
    public void onPictureTaken(byte[] image, int imageOrientation) {
        // Decode Bitmap from bytes of original image:
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // use 1 for full, no downscaled image.
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);

        if (imageOrientation > 0) {
            final Matrix matrix = new Matrix();
            matrix.setRotate(imageOrientation, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f);
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);

        }
        final ContourDetector detector = new ContourDetector();
        detector.detect(originalBitmap);
        final Bitmap documentImage = detector.processImageAndRelease(originalBitmap, detector.getPolygonF(), ContourDetector.IMAGE_FILTER_NONE);

        thumbnailImage = resizeImage(documentImage, 600, 600);
        face = thumbnailImage;
        setImage(thumbnailImage);

        Log.d("Thumb",String.valueOf(thumbnailImage));


        try {
            startActivity(ResultScanActivity.newIntent(MRZLiveDetectionActivity.this, mrzRecognitionResults));
        } finally {
            long b = System.currentTimeMillis();
        }

//        final MRZRecognitionResult mrzRecognitionResult = mrzScanner.recognizeMRZBitmap(documentImage, 0);
//        if (mrzRecognitionResult != null && mrzRecognitionResult.recognitionSuccessful) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        startActivity(ResultScanActivity.newIntent(MRZLiveDetectionActivity.this, mrzRecognitionResult));
//                    } finally {
//                        long b = System.currentTimeMillis();
//                    }
//                }
//            });
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),"Passport not valid, Please Scan Again",Toast.LENGTH_SHORT).show();
//                    Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
//                    startActivity(next);
//                }
//            });
//        }
    }

    private Bitmap resizeImage(final Bitmap bitmap, final float width, final float height) {
        final float oldWidth = bitmap.getWidth();
        final float oldHeight = bitmap.getHeight();
        final float scaleFactor = (oldWidth > oldHeight ? (width / oldWidth) : (height / oldHeight));
        final int scaledWidth = Math.round(oldWidth * scaleFactor);
        final int scaledHeight = Math.round(oldHeight * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }
    @Override
    public void onBackPressed() {
        Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(next);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MRZLiveDetectionActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
