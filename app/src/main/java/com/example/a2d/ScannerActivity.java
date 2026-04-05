package com.example.a2d;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class ScannerActivity extends AppCompatActivity {

    Button buttonOpenCamera;
    Button buttonCloseCamera;
    Button buttonGenerateSprite;
    Button buttonShowPlantDetails;
    TextView textViewPlantName;
    TextView textViewCameraHint;
    TextView textViewDownloadStatus;
    ProgressBar progressBarDownload;

    public static final String INTENT_PLANT_NAME = "Plant_Name";
    public static final int CAMERA_PERMISSION_CODE = 100;
    public final String TAG = "ScannerActivity";

    String identifiedPlantName = "";
    PlantClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Get references to widgets
        buttonOpenCamera = findViewById(R.id.buttonOpenCamera);
        buttonCloseCamera = findViewById(R.id.buttonCloseCamera);
        buttonGenerateSprite = findViewById(R.id.buttonGenerateSprite);
        buttonShowPlantDetails = findViewById(R.id.buttonShowPlantDetails);
        textViewPlantName = findViewById(R.id.textViewPlantName);
        textViewCameraHint = findViewById(R.id.textViewCameraHint);
        textViewDownloadStatus = findViewById(R.id.textViewDownloadStatus);
        progressBarDownload = findViewById(R.id.progressBarDownload);

        // Hide post-scan buttons initially
        buttonOpenCamera.setEnabled(false);
        buttonCloseCamera.setVisibility(View.GONE);
        buttonGenerateSprite.setVisibility(View.GONE);
        buttonShowPlantDetails.setVisibility(View.GONE);
        textViewPlantName.setVisibility(View.GONE);

        // Initialise classifier
        classifier = new PlantClassifier(this);

        // Download model from Hugging Face on startup
        downloadModel();

        // Open Camera button
        buttonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ScannerActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScannerActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_CODE);
                } else {
                    startCamera();
                }
            }
        });

        // Close Camera button
        buttonCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCamera();
            }
        });

        // Generate Sprite button
        buttonGenerateSprite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identifiedPlantName.isEmpty()) {
                    Toast.makeText(ScannerActivity.this,
                            "Please scan a plant first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ScannerActivity.this,
                            SpriteActivity.class);
                    intent.putExtra(INTENT_PLANT_NAME, identifiedPlantName);
                    startActivity(intent);
                }
            }
        });

        // Show Plant Details button
        buttonShowPlantDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScannerActivity.this,
                        "Details for: " + identifiedPlantName,
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Showing details for: " + identifiedPlantName);
            }
        });
    }

    private void downloadModel() {
        textViewDownloadStatus.setText("Downloading plant model...");
        textViewDownloadStatus.setVisibility(View.VISIBLE);
        progressBarDownload.setVisibility(View.VISIBLE);

        ModelDownloader.downloadModel(this, new ModelDownloader.DownloadCallback() {
            @Override
            public void onSuccess(File modelFile) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        classifier.loadModel(modelFile);
                        textViewDownloadStatus.setText("Model ready!");
                        progressBarDownload.setVisibility(View.GONE);
                        buttonOpenCamera.setEnabled(true);
                        Log.d(TAG, "Model ready for classification");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewDownloadStatus.setText("Download failed: "
                                + errorMessage);
                        progressBarDownload.setVisibility(View.GONE);
                        Toast.makeText(ScannerActivity.this,
                                "Could not load model. Check internet connection.",
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Model download failed: " + errorMessage);
                    }
                });
            }

            @Override
            public void onProgress(int progressPercent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarDownload.setProgress(progressPercent);
                        textViewDownloadStatus.setText(
                                "Downloading model... " + progressPercent + "%");
                    }
                });
            }
        });
    }

    private void startCamera() {
        textViewCameraHint.setVisibility(View.GONE);
        buttonOpenCamera.setVisibility(View.GONE);
        buttonCloseCamera.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Camera opened — tap to capture",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Camera started");

        // TODO: Replace with real CameraX capture
        // For now simulate with a test bitmap
        simulateCapture();
    }

    private void stopCamera() {
        buttonOpenCamera.setVisibility(View.VISIBLE);
        buttonCloseCamera.setVisibility(View.GONE);
        textViewCameraHint.setVisibility(View.VISIBLE);
        Log.d(TAG, "Camera stopped");
    }

    private void simulateCapture() {
        // TODO: Replace this with actual CameraX bitmap capture
        // When CameraX is set up, pass the real bitmap to classifyPlant()
        Bitmap testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888);
        classifyPlant(testBitmap);
    }

    private void classifyPlant(Bitmap capturedBitmap) {
        if (capturedBitmap == null) {
            Toast.makeText(this, "No image captured",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!classifier.isReady()) {
            Toast.makeText(this, "Model still loading, please wait",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        identifiedPlantName = classifier.classify(capturedBitmap);
        textViewPlantName.setText("Plant Identified: " + identifiedPlantName);
        textViewPlantName.setVisibility(View.VISIBLE);
        buttonShowPlantDetails.setVisibility(View.VISIBLE);
        buttonGenerateSprite.setVisibility(View.VISIBLE);
        Log.d(TAG, "Plant classified as: " + identifiedPlantName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Camera permission required to scan plants",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) {
            classifier.close();
        }
        Log.d(TAG, "onDestroy called");
    }
}