package com.example.mapapplication.Trackers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mapapplication.Events.PotholeEvent;
import com.example.mapapplication.MainActivity;
import com.example.mapapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PotholeTracker extends AppCompatActivity implements LocationListener, SensorEventListener {

    private GoogleMap myMap;
    EditText values;
    private LocationManager locationManager;
    private DatabaseReference databaseRef;
    private SensorManager sensorManager;
    private final double ACCELERATION_THRESHOLD = 25.0;
    private boolean isSensorChanged= false;

    @SuppressLint("MissingPermission")
    public PotholeTracker(@NonNull Context context, @NonNull GoogleMap map) {
        this.myMap = map;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException ex) {
            // log the error
        }
        myMap.setMyLocationEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference("pothole_locations");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Implement your location change logic here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager!=null)
        {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor!=null){
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        else
        {
            Toast.makeText(PotholeTracker.this,"Sensor is not detected",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (sensorManager != null) {
//            sensorManager.unregisterListener(listener);
//        }
    }


    private void addPotholeMarker(LatLng latLng)
        {
            myMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Pothole")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);

            ((TextView) findViewById(R.id.textvalues1)).setText("X :" + xValue + " Y :" + yValue + "Z:" + zValue);
            if (!isSensorChanged) {
                // Check if any of the sensor values exceed the default threshold
                if (xValue > -90 || yValue > 0 || zValue > -103) {
                    isSensorChanged = true; // Set the flag to indicate sensor change
                }
            } else {
                if (ActivityCompat.checkSelfPermission(PotholeTracker.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PotholeTracker.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                        int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (currentLocation != null) {
                    LatLng potholeLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    // Show a marker on the map
                    addPotholeMarker(potholeLatLng);
                    databaseRef.child("pothole_location").push().setValue(new PotholeEvent(potholeLatLng.latitude, potholeLatLng.longitude, currentLocation.getTime()));
                    // Toast a message for the user
                    Toast.makeText(PotholeTracker.this, "Pothole detected and saved", Toast.LENGTH_SHORT).show();
                }
                isSensorChanged = false; // Reset the flag after handling the sensor change


            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}



