package com.jeansandtshirt.wifive;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.LinkedHashMap;


public class WiFive extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    //SupportMapFragment mapFragment;

    WiFiveFirebase firebase;

    NavigationView navigationView;
    Toolbar toolbar;

    public MainFragment mainFragment = new MainFragment();
    public OfflineAreaSettings offlineAreaSettings = new OfflineAreaSettings();

    public Boolean mainCurrentFragment = true;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    public static Activity activity;

    public static Marker selectedMarker;

    //GoogleMap mMap;

    public FloatingActionButton fab;

    public Firebase mRef = new Firebase("https://wifivedata.firebaseio.com/");

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    LocationHandler permissionCheck = new LocationHandler();

    public Activity getActivity(){
        return activity;
    }

    public Marker getSelectedMarker() { return selectedMarker; }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void showWifisOnMap(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            firebase.showWifiAtLocation(mRef, mainFragment.mMap, new LocationHandler().getCity(getActivity(), mLastLocation));
        }
    }

    public void tempMeth(String key){
        com.firebase.client.Query query = mRef.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String URKey = "UserRatingBundleKey";//getApplicationContext().getString(R.string.user_rating_bundle_key);
                final String URMKey = "UserRatingMapBundleKey";//getApplicationContext().getString(R.string.user_rating_map_bundle_key);
                Log.d("TAG", "Again");
                LinkedHashMap userRatingsMap = getLinkedHashMap(dataSnapshot);
                String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                Integer userRating = userRatingsMap != null && userRatingsMap.get(deviceID) != null ? ((Integer) userRatingsMap.get(deviceID)) : 0;
                try{
                    Bundle bundle = new Bundle();
                    bundle.putFloat(URKey, userRating.floatValue());
                    bundle.putSerializable(URMKey, userRatingsMap);
                    android.support.v4.app.DialogFragment newFragment = new MarkerDialog();
                    //newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "MarkerDialog");

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
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"onConnected", Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.5F);

        //TODO: FIX BELOW
        //new MainFragment().showWifisOnMap();
        showWifisOnMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    public static Location getmLastLocation() {
        return mLastLocation;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/

        firebase = new WiFiveFirebase();

        // Create an instance of GoogleAPIClient.
        buildGoogleApiClient();

        //mapFragment = SupportMapFragment.newInstance();

        String WiFiSSID = new PhysicalWiFiData().getWifiName(this);

        setContentView(R.layout.activity_wi_five);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mapFragment.getMapAsync(this);

        //TODO: Margin for frame layout in landscape

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabaseHelper sqLiteDatabase = new SQLiteDatabaseHelper(getActivity().getApplicationContext());
                //boolean test = sqLiteDatabase.insertData("FDKLF243", "SSID", "pass", "MAC", "devID", 0.0, 0.0, "cityName", "userRating");
                //sqLiteDatabase.getData();

                if(new PhysicalWiFiData().isConnected(getApplicationContext()) && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    com.firebase.client.Query query = mRef.orderByChild("MAC").equalTo(new PhysicalWiFiData().getMAC(getApplicationContext()));
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                            getApplicationContext().getString(R.string.wait_title),
                            getApplicationContext().getString(R.string.wait_message),
                            true,
                            false);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                progressDialog.dismiss();
                                android.support.v4.app.DialogFragment newFragment = new WifiAlreadyExistsDialog();
                                newFragment.show(getSupportFragmentManager(), "WifiAlreadyExistsDialog");
                            }
                            else{
                                progressDialog.dismiss();
                                android.support.v4.app.DialogFragment newFragment = new AddNewWifiDialog();
                                newFragment.show(getSupportFragmentManager(), "AddNewWifiDialog");
                            }
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                else{
                    android.support.v4.app.DialogFragment newFragment = new WifiNotConnectedDialog();
                    newFragment.show(getSupportFragmentManager(), "WifiNotConnectedDialog");
                }
            }
        });

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        MainFragment mainFragment = new MainFragment();

        fragmentTransaction.add(R.id.fragment_container, mainFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        permissionCheck.checkPermissions(this, mMap, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        final String URKey = getApplicationContext().getString(R.string.user_rating_bundle_key);
        final String URMKey = getApplicationContext().getString(R.string.user_rating_map_bundle_key);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;
                String firebaseKey = new WiFiveFirebase().getEventMarkerMap().get(marker).getFirebaseKey();
                com.firebase.client.Query query = mRef.orderByKey().equalTo(firebaseKey);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LinkedHashMap userRatingsMap = getLinkedHashMap(dataSnapshot);
                        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        Integer userRating = userRatingsMap != null ? ((Integer) userRatingsMap.get(deviceID)) : 0;
                        try{
                            Bundle bundle = new Bundle();
                            bundle.putFloat(URKey, userRating.floatValue());
                            bundle.putSerializable(URMKey, userRatingsMap);
                            //TODO: UNCOMMENT WHEN READY
                            //DialogFragment newFragment = new MarkerDialog();
                            //newFragment.setArguments(bundle);
                            //newFragment.show(getFragmentManager(), "MarkerDialog");

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
                });
                return true;
            }
        });
    }*/

    public LinkedHashMap getLinkedHashMap(DataSnapshot dataSnapshot){
        Object wifi = dataSnapshot.getValue(HashMap.class).values().toArray()[0];
        LinkedHashMap userRatingsMap = (LinkedHashMap)((LinkedHashMap) wifi).get("userRatings");

        return userRatingsMap;
    }

    public Firebase getmRef() {
        return mRef;
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

                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        MainFragment mainFragment = new MainFragment();
                        mainFragment.mMap.setMyLocationEnabled(true);
                        mainFragment.showWifisOnMap();
                        //mMap.setMyLocationEnabled(true);
                        //showWifisOnMap();
                    }
                } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

                    // permission denied! Disable the
                    // functionality that depends on this permission.
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(getApplicationContext().getString(R.string.access_location_message))
                            .setTitle(getApplicationContext().getString(R.string.access_location_title));

                    builder.setPositiveButton(getApplicationContext().getString(R.string.allow), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            // User clicked Re-Try button
                        }
                    });
                    builder.setNegativeButton(getApplicationContext().getString(R.string.deny), new DialogInterface.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wi_five, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        android.support.v4.app.FragmentManager sfm = getSupportFragmentManager();
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        /*if(mapFragment.isAdded()){
            sfm.beginTransaction().hide(mapFragment).commit();
        }*/

        if (id == R.id.nav_camera) {
            /*if(!mapFragment.isAdded()){
                sfm.beginTransaction().add(R.id.map, mapFragment).commit();
            }
            else{
                sfm.beginTransaction().show(mapFragment).commit();
            }*/
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, mainFragment);
            fragmentTransaction.addToBackStack(null);

            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
            mainCurrentFragment = false;

            fragmentTransaction.commit();

        } else if (id == R.id.nav_gallery) {
            OfflineAreaSettings fragment = new OfflineAreaSettings();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
            fragmentTransaction.replace(R.id.fragment_container, offlineAreaSettings);
            fragmentTransaction.addToBackStack(null);

            mainCurrentFragment = false;

            fragmentTransaction.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
