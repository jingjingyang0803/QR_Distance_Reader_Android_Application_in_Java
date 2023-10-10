package com.example.myqrapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 42;
    private static final int QR_CODE_REQUEST_CODE = 83;

    private TextView userLocationLabel;
    private Button scanButton;
    private TextView scannedLocationLabel;
    private TextView distanceLabel;

    Location userLocation;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocationLabel = findViewById(R.id.user_lat_long);

        scannedLocationLabel = findViewById(R.id.scanned_lat_long);

        distanceLabel = findViewById(R.id.distance);

        // Initialize the scanButton
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);

        // Initialize the fusedLocationClient variable with an instance of the FusedLocationProviderClient class,
        // which provides access to the device's last known location
        // as well as the ability to request periodic location updates.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onClick(View v) {
        // Start the ScanActivity
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, QR_CODE_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if location permissions are granted
        if (checkLocationPermissions()) {
            // Get the last known location
            getLastKnownLocation();
        }
    }

    private boolean checkLocationPermissions() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Display latitude and longitude if location is available
                    userLocationLabel.setText("Current Lat: " + location.getLatitude() + "\n\nCurrent Long: " + location.getLongitude());
                    // Get the user location
                    userLocation = location;
                } else {
                    // Display "Location not available" if location is null
                    userLocationLabel.setText("Location not available");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermissions()) {
                // Get the last known location
                getLastKnownLocation();
            } else {
                // Display "Permission denied" if location permissions are not granted
                userLocationLabel.setText("Location permission required! \nPlease enable it through app settings.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the QR code scanner activity
        if (requestCode == QR_CODE_REQUEST_CODE) {
            // Check if the result is successful
            if (resultCode == RESULT_OK) {
                // Get the scanned QR code contents from the intent data
                String qrCodeContents = data.getStringExtra("qrCodeContents");

                calculateDistance(qrCodeContents, userLocation);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the case when the QR code scanning is canceled
                // Update the the user interface
                scannedLocationLabel.setText("");
                distanceLabel.setText("");
            }
        }
    }

    // The function takes two parameters: qrCodeContents, which represents the contents of the QR code,
    // and userLocation, which represents the user's current location.
    private void calculateDistance(String qrCodeContents, Location userLocation) {
        // Parse the geo: URI from the QR code contents and extract the latitude and longitude
        String geoUri = qrCodeContents.split(":")[1]; // Remove the "geo:" prefix
        String[] coordinates = geoUri.split(",");

        if (coordinates.length != 2) {
            // Invalid QR code contents, handle the error or show a message to the user
            scannedLocationLabel.setText("Invalid latitude or longitude values! Two Coordinates please!");
            distanceLabel.setText("");
        } else {
            try {

                double qrCodeLatitude = Double.parseDouble(coordinates[0]);
                double qrCodeLongitude = Double.parseDouble(coordinates[1]);

                // Ensure that the latitude is between -90 and 90, and the longitude is between -180 and 180.
                if (qrCodeLatitude < -90 || qrCodeLatitude > 90 || qrCodeLongitude < -180 || qrCodeLongitude > 180) {
                    // Invalid latitude or longitude values, handle the error or show a message to the user
                    scannedLocationLabel.setText("Invalid latitude or longitude values! Out of range error!");
                    distanceLabel.setText("");
                } else {
                    scannedLocationLabel.setText("Decoded Lat: " + String.valueOf(qrCodeLatitude) + "\n\nDecoded Long: " + String.valueOf(qrCodeLongitude));

                    // Calculate the distance between the QR code location and the user's location using the Location.distanceBetween() method
                    float[] results = new float[1];
                    Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), qrCodeLatitude, qrCodeLongitude, results);
                    float distanceInMeters = results[0];

                    // Convert the distance from meters to kilometers
                    double distanceInKilometers = distanceInMeters / 1000.0;

                    // Update the distance label in the user interface
                    distanceLabel.setText("Distance: " + distanceInKilometers + " km");
                }
            } catch (NumberFormatException e) {
                // Invalid latitude or longitude values, handle the error or show a message to the user
                scannedLocationLabel.setText("Invalid latitude or longitude values! Need Numeric Coordinates!");
                distanceLabel.setText("");
            }
        }
    }
}