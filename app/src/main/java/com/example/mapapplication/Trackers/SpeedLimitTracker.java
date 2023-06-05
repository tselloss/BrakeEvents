package com.example.mapapplication.Trackers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapapplication.Events.BrakeEvent;
import com.example.mapapplication.Events.SpeedLimitEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SpeedLimitTracker extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private LocationManager locationManager;
    private float currentSpeed;
    private DatabaseReference databaseRef;

    private boolean speedLimitEventAdded = false;
    private boolean brakeEventAdded = false;


    @SuppressLint("MissingPermission")
    public SpeedLimitTracker(@NonNull Context context, @NonNull GoogleMap map) {
        this.myMap = map;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException ex) {
            // log the error
        }
        myMap.setMyLocationEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference("speedLimit_locations");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Zoom to the current position
        // myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        currentSpeed = location.getSpeed();
        if (currentSpeed > 35) {
            if (!speedLimitEventAdded) {
                addSpeedMarker(latLng);
                // Store speed limit event in the Firebase Realtime Database
                databaseRef.push().setValue(new SpeedLimitEvent(latLng.latitude, latLng.longitude, location.getSpeed(), location.getTime()));
                speedLimitEventAdded = true;  // Mark the event as added
                brakeEventAdded = false;  // Reset the brake event flag
            }
        } else {
            // Reset both flags if the speed is within the desired range
            speedLimitEventAdded = false;
            brakeEventAdded = false;
        }
    }


    private void addSpeedMarker(LatLng latLng)
    {
        myMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Speed Limit warning")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    }

}

