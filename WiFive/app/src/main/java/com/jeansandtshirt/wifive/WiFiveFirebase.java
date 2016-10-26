package com.jeansandtshirt.wifive;

import android.app.Activity;
import android.app.DialogFragment;
import android.location.Location;
import android.provider.Settings;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by samir on 2016-07-28.
 */
public class WiFiveFirebase extends android.app.Application {

    public static HashMap<Marker, WifiWrapper> eventMarkerMap;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    public HashMap<Marker, WifiWrapper> getEventMarkerMap() {
        return eventMarkerMap;
    }

    public void showWifiAtLocation(Firebase mRef, final GoogleMap mMap, String cityName){
        eventMarkerMap = new HashMap<Marker, WifiWrapper>();

        com.firebase.client.Query query = mRef.orderByChild("city").equalTo(cityName);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                WiFi wifi = dataSnapshot.getValue(WiFi.class);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(wifi.getLatitude(), wifi.getLongitude()))
                        .title(wifi.SSID);
                Marker marker = mMap.addMarker(markerOptions);
                eventMarkerMap.put(marker, new WifiWrapper(wifi, dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                WiFi wiFi = dataSnapshot.getValue(WiFi.class);
                Iterator it = eventMarkerMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if (((WifiWrapper)pair.getValue()).getFirebaseKey().equals(dataSnapshot.getKey())){
                        ((Marker)pair.getKey()).remove();
                        break;
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addWifi(Firebase mRef, Location mLastLocation, Activity activity, String password){
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Title").draggable(false);

        PhysicalWiFiData physicalWiFiData = new PhysicalWiFiData();
        HashMap<String, Double> userRating = new HashMap<>();
        try{
            String city = new LocationHandler().getCity(activity, mLastLocation);
            String wifiName = physicalWiFiData.getWifiName(activity.getApplicationContext());
            String MAC = physicalWiFiData.getMAC(activity.getApplicationContext());
            String deviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (city != null && wifiName != null && MAC != null && deviceID != null){
                mRef.push().setValue(
                        new WiFi(wifiName,
                                password,
                                MAC,
                                deviceID,
                                mLastLocation.getLatitude(),
                                mLastLocation.getLongitude(),
                                city,
                                userRating));
            }
            else{
                DialogFragment newFragment = new NotAbleToAddWifiDialog();
                newFragment.show(activity.getFragmentManager(), "NotAbleToAddWifiDialog");
            }
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
