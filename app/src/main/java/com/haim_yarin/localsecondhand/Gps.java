package com.haim_yarin.localsecondhand;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Gps {

    private Location location;
    private double latitude, longitude ;
    private LocationManager locationManager;
    private LocationListener locationlistener;


    public Gps(LocationManager locationManager , LocationListener locationlistener)
    {
        this.locationManager = locationManager;
        this.locationlistener = locationlistener;
        configureButton();
    }

    public void configureButton() {

        try{
            locationManager.requestLocationUpdates("gps", 0, 0, locationlistener);
            locationManager.removeUpdates(locationlistener);
        }

        catch(SecurityException se)
        {
            Log.d("ERR",se.toString());
        }
    }

    public void setLocation(double latitude, double longitude, Location location){

        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;

    }

    public float distanceBetween(Location location){
        float[] results = new float[1];
        Location.distanceBetween(this.latitude,this.longitude,location.getLatitude(),location.getLongitude(),results);
        return results[0];
    }

}




