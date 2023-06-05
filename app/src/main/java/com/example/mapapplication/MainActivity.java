package com.example.mapapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapapplication.Trackers.LocationTracker;
import com.example.mapapplication.Trackers.PotholeTracker;
import com.example.mapapplication.Trackers.SpeedLimitTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private PermissionCheck permissionCheck;
    private GoogleMap myMap;
    EditText emailText, passwordText;
    FirebaseAuth firebaseAuth;
    private LocationTracker locationTracker;
    private DatabaseReference databaseRef;
    private PotholeTracker potholeTracker;
    private SpeedLimitTracker speedLimitTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("brake_locations");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("pothole_locations");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("speedLimit_locations");
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            // Initialize Google Map
            mapFragment.getMapAsync(this);
        }
    }

    // First time user has to grant permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionCheck.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionCheck.checkPermissions()) {
            initializeMap();
        }
    }

    public void go1(View view) {
        firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessage("Success", "User created successfully!");
                        definePermissionsAndMap();
                    } else {
                        showMessage("Error", task.getException().getLocalizedMessage());
                    }
                });
    }

    public void go2(View view) {
        firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessage("Success", "User signed in successfully!");
                        definePermissionsAndMap();
                    } else {
                        showMessage("Error", task.getException().getLocalizedMessage());
                    }
                });
    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public void definePermissionsAndMap() {
        setContentView(R.layout.activity_main_maps);
        permissionCheck = new PermissionCheck(this);
        if (!permissionCheck.checkPermissions()) {
            permissionCheck.requestPermissions();
        } else {
            initializeMap();
        }
        Toast.makeText(this, "All data loaded from Firebase", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        locationTracker = new LocationTracker(this, myMap);
        potholeTracker = new PotholeTracker(this, myMap);
        speedLimitTracker= new SpeedLimitTracker(this,myMap);
        // Load brake locations from Firebase and add them to the map
        databaseRef.child("brake_locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LatLng> brakeLocations = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    if (latitude != null && longitude != null) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        brakeLocations.add(latLng);
                        myMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Unexpected brake point")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        });

        // Load pothole locations from Firebase and add them to the map
        databaseRef.child("pothole_locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LatLng> potholeLocations = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    if (latitude != null && longitude != null) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        potholeLocations.add(latLng);
                        myMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Pothole")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        });

        // Retrieve speed limit from Firebase
        databaseRef.child("speedLimit_locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer speedLimit = dataSnapshot.getValue(Integer.class);
                if (speedLimit != null) {
                    // Use the speed limit value as needed
                    // For example, you can store it in a variable for later use
                    int limit = speedLimit;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation if needed
            }
        });
    }
}
