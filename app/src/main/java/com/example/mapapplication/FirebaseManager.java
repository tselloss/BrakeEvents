package com.example.mapapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String TABLE_BRAKES = "brake_locations";
    private static final String TABLE_SPEED_LIMITS = "speedLimit_locations";
    private static final String TABLE_POTHOLES = "pothole_locations";

    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_SPEED = "speed";

    private DatabaseReference brakesRef;
    private DatabaseReference speedLimitsRef;
    private DatabaseReference potholesRef;

    private List<LatLng> brakeLocations;
    private List<LatLng> potholeLocations;
    private List<LatLng> speedLimitsLocations;

    private ChildEventListener brakesChildEventListener;

    private ChildEventListener speedLimitChildEventListener;
    private ChildEventListener potholesChildEventListener;

    public FirebaseManager() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        brakesRef = databaseRef.child(TABLE_BRAKES);
        speedLimitsRef = databaseRef.child(TABLE_SPEED_LIMITS);
        potholesRef = databaseRef.child(TABLE_POTHOLES);

        brakeLocations = new ArrayList<>();
        potholeLocations = new ArrayList<>();
        speedLimitsLocations= new ArrayList<>();


        setupBrakesChildEventListener();
        setupPotholesChildEventListener();

    }

    private void setupBrakesChildEventListener() {
        brakesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Double latitude = dataSnapshot.child(COLUMN_LATITUDE).getValue(Double.class);
                Double longitude = dataSnapshot.child(COLUMN_LONGITUDE).getValue(Double.class);
                LatLng latLng = new LatLng(latitude, longitude);
                brakeLocations.add(latLng);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other child event listeners as needed for brakes

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        };
        brakesRef.addChildEventListener(brakesChildEventListener);
    }

    private void setupPotholesChildEventListener() {
        potholesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Double latitude = dataSnapshot.child(COLUMN_LATITUDE).getValue(Double.class);
                Double longitude = dataSnapshot.child(COLUMN_LONGITUDE).getValue(Double.class);
                LatLng latLng = new LatLng(latitude, longitude);
                potholeLocations.add(latLng);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other child event listeners as needed for potholes

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        };
        potholesRef.addChildEventListener(potholesChildEventListener);
    }


    private void setupSpeedLimitChildEventListener() {
        speedLimitChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Double latitude = dataSnapshot.child(COLUMN_LATITUDE).getValue(Double.class);
                Double longitude = dataSnapshot.child(COLUMN_LONGITUDE).getValue(Double.class);
                LatLng latLng = new LatLng(latitude, longitude);
                speedLimitsLocations.add(latLng);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other child event listeners as needed for potholes

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        };
        speedLimitsRef.addChildEventListener(speedLimitChildEventListener);
    }

    public boolean addBrake(double latitude, double longitude) {
        String key = brakesRef.push().getKey();
        if (key == null) {
            return false;
        }
        DatabaseReference newBrakeRef = brakesRef.child(key);
        newBrakeRef.child(COLUMN_LATITUDE).setValue(latitude);
        newBrakeRef.child(COLUMN_LONGITUDE).setValue(longitude);
        return true;
    }

    public boolean addPothole(double latitude, double longitude) {
        String key = potholesRef.push().getKey();
        if (key == null) {
            return false;
        }
        DatabaseReference newPotholeRef = potholesRef.child(key);
        newPotholeRef.child(COLUMN_LATITUDE).setValue(latitude);
        newPotholeRef.child(COLUMN_LONGITUDE).setValue(longitude);
        return true;
    }

    public boolean addSpeedLimit(double latitude, double longitude) {
        String key = speedLimitsRef.push().getKey();
        if (key == null) {
            return false;
        }
        DatabaseReference newPotholeRef = speedLimitsRef.child(key);
        newPotholeRef.child(COLUMN_LATITUDE).setValue(latitude);
        newPotholeRef.child(COLUMN_LONGITUDE).setValue(longitude);
        return true;
    }

    public List<LatLng> getAllBrakes() {
        return brakeLocations;
    }

    public List<LatLng> getAllPotholes() {
        return potholeLocations;
    }
    public List<LatLng> getAllSpeedLimits() {
        return potholeLocations;
    }

    public void cleanup() {
        if (brakesRef != null && brakesChildEventListener != null) {
            brakesRef.removeEventListener(brakesChildEventListener);
        }
        if (potholesRef != null && potholesChildEventListener != null) {
            potholesRef.removeEventListener(potholesChildEventListener);
        }
        if (speedLimitsRef != null && speedLimitChildEventListener != null) {
            speedLimitsRef.removeEventListener(speedLimitChildEventListener);
        }
    }
}

