package com.haim_yarin.localsecondhand;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Gps extends Service implements LocationListener {

    private Location location;
    private LocationManager locationManager;
    private Context context;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Gps(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void trackLocation(String provider) {
            this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // start track GPS location as soon as possible or location changed
            long minTime = 0;       // minimum time interval between location updates, in milliseconds
            float minDistance = 0;  // minimum distance between location updates, in meters
            this.locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
    }

    public double getLatitude(){
        if(this.location == null)
            return 0;
        return this.location.getLatitude();
    }
    public double getLongitude(){
        if(this.location == null)
            return 0;
        return this.location.getLongitude();
    }


    public boolean isPermissionToReadGPSLocationOK() {
        // first, check if GPS Provider (Location) is Enabled ?
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // second, check if permission to ACCESS_FINE_LOCATION is granted ?
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                showAppSettingsDialog();
                return false;
            }
        }
        else {
            showGPSSettingsDialog();
            return false;
        }
    }

    public void RemoveUpdates(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("setLoacartion", "set ok");
        locationManager.removeUpdates(this);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        context.startActivity(intent);

    }

    // Jump to GPS settings to Enabled it
    public void showGPSSettings()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    // Jump to App settings (to change the permissions if needed)
    public void showAppSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    private void showGPSSettingsDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.mipmap.ic_exit);
        alertDialog.setTitle("The applection needs GPS Enabled");
        alertDialog.setMessage("click 'ok' to move to settings");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                showGPSSettings();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(context, "GPS is NOT Enabled!", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
    }
    private void showAppSettingsDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.mipmap.ic_exit);
        alertDialog.setTitle("The applection needs Permission To Access Location");
        alertDialog.setMessage("click 'ok' to move to settings");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                showAppSettings();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(context, "NO Permission To Access Location!", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
    }


}




