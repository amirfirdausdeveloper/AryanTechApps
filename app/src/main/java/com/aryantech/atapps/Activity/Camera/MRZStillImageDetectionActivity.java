package com.aryantech.atapps.Activity.Camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
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
import net.doo.snap.util.log.Logger;
import net.doo.snap.util.log.LoggerProvider;

import io.scanbot.mrzscanner.model.MRZRecognitionResult;


public class MRZStillImageDetectionActivity extends AppCompatActivity implements PictureCallback {
    Bitmap thumbnailImage;
    private ScanbotCameraView cameraView;
    private ImageView resultImageView;
    boolean flashEnabled = false;
    private MRZScanner mrzScanner;
    private static Bitmap images;
    public void setImage(Bitmap images) {
        this.images = images;
    }
    public static Bitmap getImage() {
        return images;
    }
    Bitmap face;
    public static Intent newIntent(Context context) {
        return new Intent(context, MRZStillImageDetectionActivity.class);
    }
    private final Logger logger = LoggerProvider.getLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrz_still_image_scanner);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this)); // avoid crash

        mrzScanner = new ScanbotSDK(this).mrzScanner();
            cameraView = findViewById(R.id.cameraView);
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
            cameraView.addPictureCallback(this);
            resultImageView = findViewById(R.id.resultImageView);
            findViewById(R.id.snapButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraView.takePicture(false);
                }
            });
        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "App restarted after camera timeout", Toast.LENGTH_SHORT).show();
        }

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

        final MRZRecognitionResult mrzRecognitionResult = mrzScanner.recognizeMRZBitmap(documentImage, 0);
        if (mrzRecognitionResult != null && mrzRecognitionResult.recognitionSuccessful) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startActivity(ResultScanActivity.newIntent(MRZStillImageDetectionActivity.this, mrzRecognitionResult));
                    } finally {
                        long b = System.currentTimeMillis();
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Passport not valid, Please Scan Again",Toast.LENGTH_SHORT).show();
                    Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(next);
                }
            });
        }
    }

    private Bitmap resizeImage(final Bitmap bitmap, final float width, final float height) {
        final float oldWidth = bitmap.getWidth();
        final float oldHeight = bitmap.getHeight();
        final float scaleFactor = (oldWidth > oldHeight ? (width / oldWidth) : (height / oldHeight));
        final int scaledWidth = Math.round(oldWidth * scaleFactor);
        final int scaledHeight = Math.round(oldHeight * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    }
}
