package com.alkemic.howplace.Singletone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.alkemic.howplace.R;

public class LocationChecker {
    private static LocationChecker instance = new LocationChecker();
    public static synchronized LocationChecker getInstance()
    {
        if(instance == null)
        {
            instance = new LocationChecker();
        }
        return instance;
    }

    private LocationChecker()
    {
        activated = false;
        isGPSEnable = false;
        isNetworkEnable = false;
    }
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isGPSEnable;
    private boolean isNetworkEnable;
    private Location location;
    public static final int PERMISSION_GRANT = 1;
    public static final int PERMISSION_BASE = 0;
    public static final int PERMISSION_DENY = -1;
    private int permission = PERMISSION_BASE;
    public boolean activated = false;

    public void CheckProvider()
    {
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean Initialize(final Context context)
    {
        if(context == null) return  false;
        if(permission < 0) return  false;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null) return false;
        CheckProvider();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location newLocation) {
                location = newLocation;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                activated = true;
                Toast.makeText(context, R.string.notify_enable_location,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                activated = false;
                Toast.makeText(context,R.string.notify_disable_location,Toast.LENGTH_LONG).show();
            }
        };
        if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationChecker","Don't have permission");
            activated = false;
            return false;
        }

        if(isGPSEnable)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,100, locationListener);
            Log.d("LocationChecker","GPS provider enabled");
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        if(isNetworkEnable)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,100, locationListener);
            Log.d("LocationChecker","Network provider enabled");
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(isGPSEnable || isNetworkEnable)
        {
            activated = true;
        }
        else
        {
            Log.e("LocationChecker","Can't find any location provider");
            activated = false;
            return  false;
        }
        return  true;
    }

    public Location GetLocation()
    {
        return  this.location;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }
}
