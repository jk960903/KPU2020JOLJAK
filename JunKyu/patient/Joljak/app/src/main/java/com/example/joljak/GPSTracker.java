package com.example.joljak;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

public class GPSTracker extends Service implements LocationListener {
    private final Context context;
    Location location;
    double latitude;//위도
    double longitude;//경도
    protected LocationManager locationManager;
    LatLng currentPostion;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 6000;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();//위치 얻어오기
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gpsEnable && !networkEnable) {

            } else {
                int FineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (FineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                if (networkEnable) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (gpsEnable) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return location;
    }
    public double getLatitude(){
        if(location !=null){
            latitude=location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location!=null){
            longitude=location.getLongitude();
        }
        return longitude;
    }
    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }
    @Override
    public void onLocationChanged(Location location){
        //위도 경도 값이 바뀔때마다 호출되는 콜백 함수 1초에도 몇번씩 호출될만큼 현재 위치값 자주 변화
        //GPS 특성상 50m정도의 오차범위가 있음
        currentPostion=new LatLng(location.getLatitude(),location.getLongitude());
        longitude=currentPostion.longitude;
        latitude=currentPostion.latitude;
    }
    @Override
    public void onProviderDisabled(String provider){

    }
    @Override
    public void onProviderEnabled(String provider){

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extrase){

    }
}
