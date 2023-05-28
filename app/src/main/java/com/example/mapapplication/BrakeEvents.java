package com.example.mapapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class BrakeEvents implements LocationListener {
    private static final double DISTANCE_THRESHOLD = 2;
    private GoogleMap googleMap;
    private static final long MIN_TIME = 200;
    private static final float MIN_DISTANCE = 0;
    private float lastAvgTravelDistance = 0;
    private Location lastLocation = null;
    private LocationManager locationManager;


    @SuppressLint("MissingPermission")
    public BrakeEvents(@NonNull Context context, @NonNull GoogleMap map) {
        this.googleMap = map;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location); // distance in meters
            long timeDifference = location.getTime() - lastLocation.getTime(); // time difference in milliseconds
            if (timeDifference != 0) {
                float avgTravelDistance = (float) (distance / (timeDifference / 1000)); // average travel distance in meters/second
                if (avgTravelDistance > DISTANCE_THRESHOLD && avgTravelDistance < lastAvgTravelDistance) {
                    addMarker(latLng);
                }
                lastAvgTravelDistance = avgTravelDistance;
            }
        }
        lastLocation = location;
    }
    private void addMarker(LatLng latLng)
    {
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Unexpected brake action")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
    }

    @Override
    public void onFlushComplete(int requestCode) {
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
