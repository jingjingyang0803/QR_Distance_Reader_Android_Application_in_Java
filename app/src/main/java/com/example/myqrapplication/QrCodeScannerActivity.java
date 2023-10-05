package com.example.myqrapplication;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QrCodeScannerActivity extends AppCompatActivity {

    private BarcodeDetector barcodeDetector; // Declare the variable as a field of the class
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        // Initialize the QR code detector
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        // Initialize the camera source
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).build();

        // Start the camera preview
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the camera source
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop the camera source
        cameraSource.stop();
    }

    private class QRCodeDetectorProcessor implements Detector.Processor<Barcode> {

        @Override
        public void release() {
            // Release any resources used by the detector processor
        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            // Get the detected QR codes
            SparseArray<Barcode> qrCodes = detections.getDetectedItems();

            // Process each detected QR code
            for (int i = 0; i < qrCodes.size(); i++) {
                Barcode qrCode = qrCodes.valueAt(i);

                // Handle the scanned QR code contents
                String contents = qrCode.displayValue;
                // ...
            }
        }
    }

    private void startQRCodeScan() {
        // Create a new instance of the QRCodeDetectorProcessor
        QRCodeDetectorProcessor qrCodeDetectorProcessor = new QRCodeDetectorProcessor() {
            public void onQRCodeDetected(String qrCodeContents) {
                // Handle the scanned QR Code contents
                System.out.println("Scanned QR Code contents: " + qrCodeContents);
            }
        };

        // Set the QRCodeDetectorProcessor as the processor for the barcode detector
        barcodeDetector.setProcessor(qrCodeDetectorProcessor);
    }


}
