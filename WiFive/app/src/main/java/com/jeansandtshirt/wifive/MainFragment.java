package com.jeansandtshirt.wifive;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tagmanager.Container;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MainFragment extends Fragment implements
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback
        {

    private GoogleApiClient mGoogleApiClient;

    LocationHandler permissionCheck = new LocationHandler();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    public Firebase mRef = new Firebase("https://wifivedata.firebaseio.com/");

    public static Marker selectedMarker;
    public static GoogleMap mMap;

    SupportMapFragment mapFragment;

    WiFiveFirebase firebase;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebase = new WiFiveFirebase();

        //mGoogleApiClient.connect();

        mapFragment = SupportMapFragment.newInstance();

        mapFragment.getMapAsync(this);
        android.support.v4.app.FragmentManager sfm = getFragmentManager();

        sfm.beginTransaction().add(R.id.wifive_map, mapFragment).commit();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Create an instance of GoogleAPIClient.
        //buildGoogleApiClient();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        permissionCheck.checkPermissions(getActivity(), mMap, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        final String URKey = getContext().getString(R.string.user_rating_bundle_key);
        final String URMKey = getContext().getString(R.string.user_rating_map_bundle_key);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;
                String firebaseKey = new WiFiveFirebase().getEventMarkerMap().get(marker).getFirebaseKey();
                com.firebase.client.Query query = mRef.orderByKey().equalTo(firebaseKey);
                new WiFive().tempMeth(firebaseKey);
                /*query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LinkedHashMap userRatingsMap = getLinkedHashMap(dataSnapshot);
                        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        Integer userRating = userRatingsMap != null ? ((Integer) userRatingsMap.get(deviceID)) : 0;
                        try{
                            Bundle bundle = new Bundle();
                            bundle.putFloat(URKey, userRating.floatValue());
                            bundle.putSerializable(URMKey, userRatingsMap);
                            android.support.v4.app.DialogFragment newFragment = new MarkerDialog();
                            //newFragment.setArguments(bundle);
                            newFragment.show(getFragmentManager(), "MarkerDialog");

                            //new MarkerDialog().CreateMarkerDialog(userRating.floatValue(), userRatingsMap);

                            //new GetUserData().getProgressDialog().dismiss();
                        }
                        catch (Exception e){
                            Log.d("Exception: ", e.getMessage().toString());
                        }

                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/
                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /*protected synchronized void buildGoogleApiClient() {
    Toast.makeText(getContext(), "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
    mGoogleApiClient = new GoogleApiClient.Builder(getContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }*/

    public void showWifisOnMap(){
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            /*LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            firebase.showWifiAtLocation(mRef, mMap, new LocationHandler().getCity(getActivity(), mLastLocation));*/
        }
    }

    public LinkedHashMap getLinkedHashMap(DataSnapshot dataSnapshot){
        Object wifi = dataSnapshot.getValue(HashMap.class).values().toArray()[0];
        LinkedHashMap userRatingsMap = (LinkedHashMap)((LinkedHashMap) wifi).get("userRatings");

        return userRatingsMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted! Do the
                    // contacts-related task you need to do.

                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        showWifisOnMap();
                    }
                } else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){

                    // permission denied! Disable the
                    // functionality that depends on this permission.
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(getContext().getString(R.string.access_location_message))
                            .setTitle(getContext().getString(R.string.access_location_title));

                    builder.setPositiveButton(getContext().getString(R.string.allow), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            // User clicked Re-Try button
                        }
                    });
                    builder.setNegativeButton(getContext().getString(R.string.deny), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
