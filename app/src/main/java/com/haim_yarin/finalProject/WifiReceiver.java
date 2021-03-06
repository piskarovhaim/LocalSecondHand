package com.haim_yarin.finalProject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

// listen to the WIFI and manege tracking location with network or gps
public class WifiReceiver extends BroadcastReceiver {

    private Gps gps;
    private boolean locationTrackByWifi;

    public WifiReceiver(Gps gps){
        this.gps=gps;
        this.locationTrackByWifi = true;
    }

    public String getProvider(){
        if(locationTrackByWifi)
            return LocationManager.NETWORK_PROVIDER;
        return LocationManager.GPS_PROVIDER;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {

            Toast.makeText(context, "Track Location by Network", Toast.LENGTH_SHORT).show();

            locationTrackByWifi = true;
            if(!locationTrackByWifi)
                gps.trackLocation(LocationManager.NETWORK_PROVIDER);


        }
        else {
            Toast.makeText(context, "Track Location by GPS", Toast.LENGTH_SHORT).show();
            locationTrackByWifi = false;
            if(locationTrackByWifi)
                gps.trackLocation(LocationManager.GPS_PROVIDER);
        }
    }
}
