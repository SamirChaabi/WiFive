package com.jeansandtshirt.wifive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by samir on 2016-08-06.
 */
public class MarkerDialog extends android.support.v4.app.DialogFragment {

    public static View markerDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        String URKey = this.getString(R.string.user_rating_bundle_key);
        String URMKey = this.getString(R.string.user_rating_map_bundle_key);

        Float userRating = (Float)bundle.getFloat(URKey);
        LinkedHashMap userRatingsList = (LinkedHashMap)bundle.getSerializable(URMKey);

        HashMap<Marker, WifiWrapper> eventMarkerMap = new WiFiveFirebase().getEventMarkerMap();
        WifiWrapper wiFiWrapper = eventMarkerMap.get(new WiFive().getSelectedMarker());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        markerDialog = inflater.inflate(R.layout.marker_fragment, null);
        ratingbarListener(wiFiWrapper);

        final TextView title = (TextView) markerDialog.findViewById(R.id.MarkerTitle);
        title.setText(wiFiWrapper.getWiFi().getSSID());

        //Get EditText in Dialog
        final TextView password = (TextView) markerDialog.findViewById(R.id.MarkerPassword);

        final TextView rating = (TextView) markerDialog.findViewById(R.id.textViewRating);
        String ratingTextView = this.getString(R.string.rating);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ratingTextView).append(" ").append(CalculateAverageUserRating(userRatingsList));
        rating.setText(stringBuilder);

        final RatingBar ratingBar = (RatingBar) markerDialog.findViewById(R.id.ratingBar);
        if (userRating != 0)
            ratingBar.setRating(userRating);

        password.setText(wiFiWrapper.getWiFi().getPassword());

        builder.setView(markerDialog);
        builder//.setTitle(wiFiWrapper.getWiFi().getSSID())
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
    public Double CalculateAverageUserRating(LinkedHashMap userRatingsMap) {
        Double averageUserRating = 0.0;
        if(userRatingsMap != null){
            for (Object entry : userRatingsMap.entrySet()) {
                Map.Entry userRating = (Map.Entry)entry;
                Integer tempUserRating = (Integer) userRating.getValue();
                averageUserRating += userRating.getValue() != null ? tempUserRating.doubleValue() : 0.0;
            }
            averageUserRating /= userRatingsMap.size();

            return Math.round(averageUserRating * 10.0) / 10.0;
        }
        else{
            return 0.0;
        }
    }

    public static MarkerDialog newInstance() {
        
        Bundle args = new Bundle();
        
        MarkerDialog fragment = new MarkerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void ratingbarListener(final WifiWrapper wifiWrapper){
        final Firebase mRef = new WiFive().getmRef();
        RatingBar ratingBar = (RatingBar) markerDialog.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            WiFiveFirebase wiFiveFirebase = new WiFiveFirebase();
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setRating(wifiWrapper, Math.round(ratingBar.getRating()), mRef);
                updateUserRatings(wifiWrapper, mRef);
            }
        });
    }

    public void setRating(WifiWrapper wifiWrapper, int rating, Firebase mRef){

        Firebase markerRef = mRef.child(wifiWrapper.getFirebaseKey()).child("userRatings");

        Map<String, Object> newRating = new HashMap<String, Object>();
        newRating.put(Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID), rating);
        try{
            markerRef.updateChildren(newRating);
        }
        catch (Exception e){
            Log.d("Exception Message: ", e.toString());
        }
    }

    public void updateUserRatings(WifiWrapper wifiWrapper, Firebase mRef){
        com.firebase.client.Query query = mRef.orderByKey().equalTo(wifiWrapper.getFirebaseKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Find Marker dialog and change rating (TextView)
                try{
                    TextView averageUserRatingTextView = (TextView)getDialog().findViewById(R.id.textViewRating);

                    String averageUserRating = new MarkerDialog()
                            .CalculateAverageUserRating(getLinkedHashMap(dataSnapshot))
                            .toString();

                    String ratingTextViewText = new WiFive().getActivity().getString(R.string.rating);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(ratingTextViewText).append(" ").append(averageUserRating);
                    averageUserRatingTextView.setText(stringBuilder);
                }
                catch (Exception e){
                    Log.d("Exception: ", e.getMessage());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public LinkedHashMap getLinkedHashMap(DataSnapshot dataSnapshot){
        Object wifi = dataSnapshot.getValue(HashMap.class).values().toArray()[0];
        return (LinkedHashMap)((LinkedHashMap) wifi).get(this.getString(R.string.user_ratings_node));
    }
}
