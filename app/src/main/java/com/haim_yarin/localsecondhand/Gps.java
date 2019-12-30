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

    private float latitude, Longitude ;
    private LocationManager locationManager;
    private LocationListener locationlistener;


    public Gps(LocationManager locationManager , LocationListener locationlistener)
    {

        this.locationManager = locationManager;
        this.locationlistener = locationlistener;
        configureButton();
    }




    public void configureButton() {

        locationManager.requestLocationUpdates("gps", 0, 0, locationlistener);
        locationManager.removeUpdates(locationlistener);

    }


}




