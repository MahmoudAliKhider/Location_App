package com.example.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;


import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.Touch;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient mFusedLocationClient;
    TextView lat;
    TextView lon;
    public static final int LOCATION=1;
    public static final int MAP=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        lat=findViewById( R.id.lat );
        lon=findViewById( R.id.lon );
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient( this );

    }
    @Override
    protected void onResume(){
        super.onResume();
        getLastLocation();
    }
    private boolean checkPermission(){
    if(ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED&&
    ActivityCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED){
        return true;
    }
    return false;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

            if (requestCode==LOCATION){
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                }
            }
    }
private boolean isLocationEnable(){
        LocationManager locationManager=(LocationManager) getSystemService( Context.LOCATION_SERVICE );
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
}

    @SuppressLint( "MissingPermission" )
    private void getLastLocation(){
        if(checkPermission( )){
            if (isLocationEnable()){
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location=task.getResult();
                            if(location==null){
                                requestNewLocationData();
                            }else{
                                lat.setText( location.getLatitude()+"" );
                                lon.setText( location.getLongitude()+"" );
                            }
                        }
                    }

            );
            }else{
                Toast.makeText( this, "Turn on location", Toast.LENGTH_SHORT ).show();
                Intent intent=new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                startActivity( intent );
            }
        }else{
            requestPermission();
        }
    }


    @SuppressLint( "MissingPermission" )

    private void requestNewLocationData(){
        LocationRequest mLocationRequest=new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval( 1 );

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient( this );
        mFusedLocationClient.requestLocationUpdates( mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );


    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i("call",locationResult.getLastLocation()+"");
        }
    };
}