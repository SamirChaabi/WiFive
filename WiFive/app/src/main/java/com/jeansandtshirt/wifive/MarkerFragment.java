package com.jeansandtshirt.wifive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by samir on 2016-08-22.
 */
public class MarkerFragment extends MapFragment {

    public Marker placeMarker(WiFi wiFi, GoogleMap mMap){
        Marker m = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(wiFi.getLatitude(), wiFi.getLongitude()))
                .title(wiFi.SSID));

        return m;
    }
}
