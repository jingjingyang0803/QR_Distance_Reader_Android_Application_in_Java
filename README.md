## My QR Application

This is an Android application that allows users to scan QR codes and calculate the distance between their current location and the location encoded in the QR code.

### Features

- Scan QR codes: The app uses the device's camera to scan QR codes.
- Location tracking: The app retrieves the user's current location using the device's GPS capabilities.
- Distance calculation: The app calculates the distance between the user's location and the location encoded in the scanned QR code.
- User interface: The app displays the user's current location, the scanned location from the QR code, and the calculated distance.

### Prerequisites

- Android device running Android 6.0 (Marshmallow) or higher.
- Camera permission: The app requires permission to access the device's camera for QR code scanning.
- Location permission: The app requires permission to access the device's location for retrieving the user's current location.

### Installation

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Build and run the app on your Android device.

### Usage

1. Launch the app on your device.
2. Grant camera and location permissions if prompted.
3. The app will display your current location.
4. Press the "Scan" button to scan a QR code.
5. Point your device's camera at the QR code to scan it.
6. Press the "Finish" button to close the QR code scanning activity.
7. The app will display the scanned location and the calculated distance from your current location.

### Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

### License

This project is licensed under the [MIT License](notion://www.notion.so/LICENSE).

### Acknowledgements

This project uses the following libraries:

- Google Play Services: Location and FusedLocationProviderClient
- Google Mobile Vision: BarcodeDetector and CameraSource
