package com.example.myqrapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.util.SparseArray;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    SurfaceView surfaceViewLabel;
    TextView barcodeValueLabel;
    Button finishButton;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        surfaceViewLabel = findViewById(R.id.surfaceView);
        barcodeValueLabel = findViewById(R.id.txtBarcodeValue);
        finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "Finish" button click event
                // Finish the activity
                finish();
            }
        });

        // Initialize barcode detectors and camera sources
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        // Create a barcode detector with QR_CODE format
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        // Create a camera source with autofocus and preferred preview size
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();

        // Set up the SurfaceView for the camera preview
        surfaceViewLabel.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // Check camera permission and start the camera preview
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceViewLabel.getHolder());
                    } else {
                        // Request camera permission if not granted
                        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Not used in this example
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Stop the camera when the SurfaceView is destroyed
                cameraSource.stop();
            }
        });

        // Set up the barcode detector processor
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Release resources to prevent memory leaks
                Toast.makeText(getApplicationContext(),
                        "To prevent memory leaks barcode scanner has been stopped",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                // Process detected barcodes and update UI on the main thread
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeValueLabel.post(new Runnable() {
                        @Override
                        public void run() {

                            String contents = barcodes.valueAt(0).displayValue;

                            // Create a new intent to pass the scanned QR code contents back to the MainActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("qrCodeContents", contents);

                            // Set the result as OK and pass the result intent back to the MainActivity
                            setResult(RESULT_OK, resultIntent);

                            // Update the UI with the barcode value
                            barcodeValueLabel.setText("Scanned Contents:\n " + contents);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release camera resources when the activity is paused
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    // Handle the result of the camera permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, start the camera preview
                try {
                    cameraSource.start(surfaceViewLabel.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Camera permission denied, display a message or take appropriate action
                barcodeValueLabel.setText("Camera permission required!\nPlease enable it through app settings.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release barcode detector resources when the activity is destroyed
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
    }
}