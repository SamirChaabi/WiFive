package com.jeansandtshirt.wifive;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
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
public class MainFragment extends Fragment
        {

    public Firebase mRef = new Firebase("https://wifivedata.firebaseio.com/");

    public static Marker selectedMarker;

    SupportMapFragment mapFragment;

    WiFiveFirebase firebase;

    public MainFragment() {
        // Required empty public constructor
    }

    public void setSupportMapFragment(SupportMapFragment mapFragment){
        this.mapFragment = mapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebase = new WiFiveFirebase();

        android.support.v4.app.FragmentManager sfm = getFragmentManager();

        sfm.beginTransaction().add(R.id.wifive_map, mapFragment).commit();

        //View rootView = inflater.inflate(R.layout.fragment_main, container, false);

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

    public LinkedHashMap getLinkedHashMap(DataSnapshot dataSnapshot){
        Object wifi = dataSnapshot.getValue(HashMap.class).values().toArray()[0];
        LinkedHashMap userRatingsMap = (LinkedHashMap)((LinkedHashMap) wifi).get("userRatings");

        return userRatingsMap;
    }
}
