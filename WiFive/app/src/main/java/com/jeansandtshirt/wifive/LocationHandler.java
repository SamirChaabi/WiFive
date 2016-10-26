package com.jeansandtshirt.wifive;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.List;

/**
 * Created by samir on 2016-07-21.
 */
public class LocationHandler{

    public void checkPermissions(Activity activity, GoogleMap googleMap, int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Location permission has not been granted. Asking for permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        requestCode);
                //IF DENIED INFORM USER THAT IT HAS BEEN DENIED
            } else {
                // Location permissions is already available, show the location.
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            googleMap.setMyLocationEnabled(true);
        }
    }

    public Location getLocation(Activity activity, LocationManager locationManager){

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            return location;
        }
        return null;
    }
    public String getCity(Activity activity, Location location){
        String locality;
        Geocoder gc = new Geocoder(activity.getApplicationContext());
        try {
            if (location != null) {
                List<Address> list = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 50);
                for (Address address : list) {
                    if (address.getLocality() != null) {
                        return address.getLocality();
                    }
                }
                return null;
            }
            else
                return null;
        }
        catch (IOException e){
            return "Not found!";
        }
    }
}
