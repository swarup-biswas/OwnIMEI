package com.example.ownimei.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ownimei.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class DevicePosition extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean mLocationPermissonGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_position);
        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissonGranted) {
            getDeviceLocation();
            if ((ContextCompat.checkSelfPermission(DevicePosition.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    && ActivityCompat.checkSelfPermission(DevicePosition.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_device);
        mapFragment.getMapAsync(DevicePosition.this);
    }

//    public void moveCamera(LatLng latLng, int zoom) {
//        // Add a marker in Sydney and move the camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        LatLng dhaka = latLng;
//        mMap.addMarker(new MarkerOptions().position(dhaka).title("Marker in Dhaka"));
//    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DevicePosition.this);

        try {
            if (mLocationPermissonGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = task.getResult();
                            try {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), 15));
//                            LatLng dhaka = latLng;
                                mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("Marker in Dhaka"));

//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15);
                            }catch (Exception e){
                                Toast.makeText(DevicePosition.this, "1"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(DevicePosition.this, "Unable to get current location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(DevicePosition.this, "2" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }



    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(DevicePosition.this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(DevicePosition.this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissonGranted = true;
                initMap();


            } else {
                ActivityCompat.requestPermissions(DevicePosition.this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(DevicePosition.this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissonGranted = false;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

