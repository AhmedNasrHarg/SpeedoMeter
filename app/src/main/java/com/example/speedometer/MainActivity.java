package com.example.speedometer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int REQUEST_CHECK_SETTINGS=2;
    int PERMISSION_ID=1;
    FusedLocationProviderClient mFusedLocationClient;
    Location mCurrentLocation;
    LocationRequest locationRequest;
    private LocationCallback locationCallback;
    TextView speed;
    TextView timeToTen;
    TextView timeFromTen;
    TextView toTen;
    TextView fromTen;
    long prevTime;
    float prevSpeed;

    float initSpeed = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speed=findViewById(R.id.speedID);
        timeFromTen=findViewById(R.id.timeFromTen);
        timeToTen=findViewById(R.id.timeToTen);
        toTen=findViewById(R.id.toTen);
        fromTen=findViewById(R.id.fromTen);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                    // Update UI with location data
                    float curSpeedValue = (float)((location.getSpeed()*3.6)*10000/10000);
                    speed.setText(curSpeedValue+""); //formatting


//                    long curTime = System.currentTimeMillis()/1000L;
//                    long differenceInTime=curTime-prevTime;
//                    prevTime=curTime;
//
//                    float speedDifference = curSpeedValue-initSpeed;
//                    float acc= speedDifference/differenceInTime;
//                    initSpeed=curSpeedValue;
//
//                    int neededTimeFrom10 = (int) ((2*acc*(initSpeed+curSpeedValue))/((curSpeedValue*curSpeedValue)-initSpeed*initSpeed));
//                    timeFromTen.setText(neededTimeFrom10+"");
//                    fromTen.setText("From 10 to "+(int)curSpeedValue);
//                    initSpeed=curSpeedValue;
//
//                    int neededTimeTo10= (int) ((2*acc*(10.0+curSpeedValue))/(100.0-curSpeedValue*curSpeedValue));
//                    timeToTen.setText((int)((10-curSpeedValue)/acc)+"");
//                    toTen.setText("From "+(int)curSpeedValue+" to 10");
//                    Toast.makeText(MainActivity.this,((2*acc*(10+curSpeedValue))/(100-curSpeedValue*curSpeedValue))+"",Toast.LENGTH_LONG).show();

            }
        };
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }
    protected void createLocationRequest() {

         locationRequest = LocationRequest.create();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>(){

            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
                task.addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            Toast.makeText(MainActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }
                });



    }
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    public void onResume(){
        super.onResume();
        createLocationRequest();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
