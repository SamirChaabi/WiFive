package com.jeansandtshirt.wifive;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by samir on 2016-10-27.
 */
public class MapsFragment extends android.app.Application implements OnMapReadyCallback {

    public GoogleMap mMap;
    LocationHandler permissionCheck;
    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        permissionCheck = new LocationHandler();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        permissionCheck.checkPermissions(new WiFive().getActivity(), mMap, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }
}
