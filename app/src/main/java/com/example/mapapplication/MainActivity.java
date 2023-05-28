package com.example.mapapplication;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText emailText, passwordText;
    FirebaseAuth firebaseAuth;
    private GoogleMap googleMap;
    private BrakeEvents brakeEvents;
    private PermissionCheck permissionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate called");
        emailText= findViewById(R.id.editTextTextEmailAddress);
        passwordText= findViewById(R.id.editTextTextPassword);
        firebaseAuth= FirebaseAuth.getInstance();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        brakeEvents = new BrakeEvents(this, googleMap);
        LatLng koropi= new LatLng(37.897230, 23.862740);
        addBrakeMarker(koropi);
    }

    void addBrakeMarker(LatLng location) {
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Unexpected brake action"));
    }

    public void go1(View view){
        firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        showMessage("Success","User created successfully!");
                        nextSteps();
                    } else {
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                });
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void go2(View view){
        firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        showMessage("Success","User signed in successfully!");
                       nextSteps();
                    } else {
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                });
        //auth.signOut();
    }

    public void nextSteps()
    {
        setContentView(R.layout.activity_main_maps);
        permissionCheck = new PermissionCheck(this);
        if (!permissionCheck.checkPermissions()) {
            permissionCheck.requestPermissions();
        } else {
            initializeMap();
        }
        Toast.makeText(this, "All data loaded from Firebase", Toast.LENGTH_SHORT).show();
    }

}
