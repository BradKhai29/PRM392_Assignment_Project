package com.example.prm392_assignment_project.views.screens;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageButton btnBackHome;
    private boolean allowToAccessCoarseLocation = false;
    private static final LatLng SHOP_POSITION = new LatLng(15.969205643909286, 108.26086983817157);
    private static final String SHOP_MARKER_TITLE = "NUT SHOP";

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

        // Bind components from view.
        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(this::backHome);

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

        if (!checkAccessCoarseLocationPermission())
        {
            requestAccessCoarseLocationPermission();
        }
    }

    private void backHome(View view)
    {
        finish();
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        LocationManager locationManager = getSystemService(LocationManager.class);

        if (locationManager == null)
        {
            Toast.makeText(this, "Location manager is null now", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkAccessCoarseLocationPermission())
        {
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentLocation == null)
            {
                Toast.makeText(this, "Location is null now", Toast.LENGTH_SHORT).show();
                return;
            }

            showUserCoarseLocation(currentLocation, googleMap);
        }

        showShopLocation(googleMap);
    }

    private void showUserCoarseLocation(Location currentLocation, GoogleMap googleMap)
    {
        // Get the latitude and longitude of the location.
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();
        LatLng currentPosition = new LatLng(latitude, longitude);

        MarkerOptions currentPositionMarker = new MarkerOptions().position(currentPosition).title("My Location");
        googleMap.addMarker(currentPositionMarker);
    }

    private void showShopLocation(GoogleMap googleMap)
    {
        MarkerOptions shopPositionMarker = new MarkerOptions().position(SHOP_POSITION).title(SHOP_MARKER_TITLE);
        googleMap.addMarker(shopPositionMarker);

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
        cameraPositionBuilder.target(SHOP_POSITION);
        cameraPositionBuilder.zoom(16);

        CameraPosition cameraPosition = cameraPositionBuilder.build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            // Check the grant result and ensure it contains permission granted value.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                allowToAccessCoarseLocation = true;
            }
        }
    }

    private boolean checkAccessCoarseLocationPermission()
    {
        allowToAccessCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;

        return allowToAccessCoarseLocation;
    }

    private void requestAccessCoarseLocationPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

            alertBuilder.setMessage("Vui lòng cho phép app truy cập vào vị trí hiện tại của bạn để tính năng này hoạt động bình thường");
            alertBuilder.setTitle("Cấp quyền truy cập vị trí");
            alertBuilder.setPositiveButton("Cho phép", this::onClickToAcceptPermission);
            alertBuilder.setNegativeButton("Đóng", this::onClickToRejectPermission);

            alertBuilder.show();
        }
        else
        {
            requestRuntimePermission();
        }
    }

    private void requestRuntimePermission()
    {
        ActivityCompat.requestPermissions(
            this,
            new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
            PERMISSION_REQUEST_CODE);
    }

    private void onClickToAcceptPermission(DialogInterface dialog, int which)
    {
        requestRuntimePermission();
        dialog.dismiss();
    }

    private void onClickToRejectPermission(DialogInterface dialog, int which)
    {
        dialog.dismiss();
    }
}