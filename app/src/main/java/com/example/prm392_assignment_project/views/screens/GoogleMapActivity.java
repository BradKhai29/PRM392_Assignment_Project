package com.example.prm392_assignment_project.views.screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_assignment_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Permission;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_google_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }
        else
        {
            Toast.makeText(this, "Show map failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LocationManager locationManager = getSystemService(LocationManager.class);

        if (locationManager == null)
        {
            Toast.makeText(this, "Location manager is null now", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean accessCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;

        if (accessCoarseLocationPermissionGranted)
        {
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentLocation == null)
            {
                Toast.makeText(this, "Location is null now", Toast.LENGTH_SHORT).show();
                return;
            }

            final LatLng shopPosition = new LatLng(15.969205643909286, 108.26086983817157);

            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            LatLng currentPosition = new LatLng(latitude, longitude);

            MarkerOptions currentPositionMarker = new MarkerOptions().position(currentPosition).title("My Location");
            MarkerOptions shopPositionMarker = new MarkerOptions().position(shopPosition).title("Nut shops Location");
            googleMap.addMarker(currentPositionMarker);
            googleMap.addMarker(shopPositionMarker);

            CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
            cameraPositionBuilder.target(currentPosition);
            cameraPositionBuilder.zoom(16);
            cameraPositionBuilder.bearing(currentLocation.getBearing());
//            cameraPositionBuilder.tilt(70);

            CameraPosition cameraPosition= cameraPositionBuilder.build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}