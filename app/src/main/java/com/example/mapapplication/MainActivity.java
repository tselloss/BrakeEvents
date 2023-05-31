package com.example.mapapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    EditText emailText, passwordText;
    FirebaseAuth firebaseAuth;
    private GoogleMap googleMap;
    private Location location;
    private PermissionCheck permissionCheck;
    private Marker currentLocationMaker;
    private LatLng currentLocationLatLong;
    private DatabaseReference mDatabase;
    Circle circle;
    private static final double DISTANCE_THRESHOLD = 2.5;
    private GoogleMap mMap;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 0;
    private float lastAvgTravelDistance = 0;
    private Location lastLocation = null;
    private LocationManager mLocationManager;

    private ArrayList<UserInfo> mMapLocations = new ArrayList<UserInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate called");
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseLoadData();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng somepoint = new LatLng(37.896430,23.866650 );
        addBrakeMarker(somepoint);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(somepoint));
    }



    void addBrakeMarker(LatLng location) {
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Unexpected brake action"));
    }

    public void go1(View view) {
        firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessage("Success", "User created successfully!");
                        nextSteps();
                    } else {
                        showMessage("Error", task.getException().getLocalizedMessage());
                    }
                });
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void go2(View view) {
        firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showMessage("Success", "User signed in successfully!");
                        nextSteps();
                    } else {
                        showMessage("Error", task.getException().getLocalizedMessage());
                    }
                });
    }

    public void nextSteps() {
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
    public void onLocationChanged(@NonNull Location location) {
        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location); // distance in meters
            long timeDifference = location.getTime() - lastLocation.getTime(); // time difference in milliseconds
            float avgTravelDistance = (float) (distance / (timeDifference / 1000)); // average travel distance in meters/second
            if (lastAvgTravelDistance - avgTravelDistance > DISTANCE_THRESHOLD) {
                //Add marker
                currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentLocationLatLong);
                markerOptions.title("Unexpected brake");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                currentLocationMaker = googleMap.addMarker(markerOptions);
                UserInfo locationData = new UserInfo(location.getLatitude(), location.getLongitude());
                mDatabase.child("brakeLocation").child(String.valueOf(new Date().getTime())).setValue(locationData);
                Toast.makeText(this, "Firebase save location", Toast.LENGTH_SHORT).show();
            }
            lastAvgTravelDistance = avgTravelDistance;
            getMarkers();
        }
        lastLocation = location;
    }
    private void getMarkers() {
        mDatabase.child("brakeLocation").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String, Object>) dataSnapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void getAllLocations(Map<String, Object> locations) {
        for (Map.Entry<String, Object> entry : locations.entrySet()) {
            Date newDate = new Date(Long.valueOf(entry.getKey()));
            Map singleLocation = (Map) entry.getValue();
            LatLng latLng = new LatLng((Double) singleLocation.get("latitude"), (Double) singleLocation.get("longitude"));
            addBrakeMarker(newDate, latLng);
        }
    }

    private void addBrakeMarker(Date newDate, LatLng latLng) {
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(dt.format(newDate));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerOptions);
    }



    private void firebaseLoadData(){
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    Double lattitude = locationSnapshot.child("lattitude").getValue(Double.class);
                    Double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    Log.d("FirebaseLoadData", " lattitude: " +
                            lattitude + " longitude: " + longitude);
                    mMapLocations.add(new UserInfo(lattitude, longitude));
                }
                createMarkersFromFirebase(mMapLocations);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    private void createMarkersFromFirebase(ArrayList<UserInfo> locations){
        for(UserInfo location : locations){
            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(newLocation).title("Brake point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
    }


}