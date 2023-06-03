package com.example.mapapplication;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String TABLE_NAME = "brakes";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private DatabaseReference databaseRef;
    private List<LatLng> breakLocations;
    private ChildEventListener childEventListener;

    public FirebaseManager() {
        databaseRef = FirebaseDatabase.getInstance().getReference(TABLE_NAME);
        breakLocations = new ArrayList<>();
        setupChildEventListener();
    }

    private void setupChildEventListener() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Double latitude = dataSnapshot.child(COLUMN_LATITUDE).getValue(Double.class);
                Double longitude = dataSnapshot.child(COLUMN_LONGITUDE).getValue(Double.class);
                LatLng latLng = new LatLng(latitude, longitude);
                breakLocations.add(latLng);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child changed if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle child removed if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child moved if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        };
        databaseRef.addChildEventListener(childEventListener);
    }

    public boolean addBreak(double latitude, double longitude) {
        String key = databaseRef.push().getKey();
        if (key == null) {
            return false;
        }
        DatabaseReference newBreakRef = databaseRef.child(key);
        newBreakRef.child(COLUMN_LATITUDE).setValue(latitude);
        newBreakRef.child(COLUMN_LONGITUDE).setValue(longitude);
        return true;
    }

    public List<LatLng> getAllBreaks() {
        return breakLocations;
    }

    public void cleanup() {
        if (databaseRef != null && childEventListener != null) {
            databaseRef.removeEventListener(childEventListener);
        }
    }
}
