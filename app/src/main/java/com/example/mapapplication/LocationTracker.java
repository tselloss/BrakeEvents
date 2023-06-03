package com.example.mapapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationTracker implements LocationListener {
    private GoogleMap myMap;
    private LocationManager locationManager;
    private Location previousLocation = null;
    private DatabaseReference databaseRef;
    private static final double DISTANCE_THRESHOLD = 6;

    private static final long MIN_TIME = 300;
    private static final float MIN_DISTANCE = 0;
    private float previousAverageTravelDistance = 0;


    @SuppressLint("MissingPermission")
    public LocationTracker(@NonNull Context context, @NonNull GoogleMap map) {
        this.myMap = map;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } catch (SecurityException ex) {
            // log the error
        }
        myMap.setMyLocationEnabled(true);
        databaseRef = FirebaseDatabase.getInstance().getReference("brake_locations");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        if (previousLocation != null) {
            float distance = previousLocation.distanceTo(location); // distance in meters
            long timeDifference = location.getTime() - previousLocation.getTime(); // time difference in milliseconds
            float avgTravelDistance = (float) (distance / (timeDifference/1000)); // average travel distance in meters/second

            if ((previousAverageTravelDistance - avgTravelDistance) > DISTANCE_THRESHOLD) {
                addBrakeMarker(latLng);
                // Store brake event in the Firebase Realtime Database
                databaseRef.push().setValue(new BrakeEvent(latLng.latitude, latLng.longitude, location.getSpeed(), location.getTime()));
            }
            previousAverageTravelDistance = avgTravelDistance;
        }
        previousLocation = location;
    }

    private void addBrakeMarker(LatLng latLng)
    {
        myMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Unexpected brake action")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}

