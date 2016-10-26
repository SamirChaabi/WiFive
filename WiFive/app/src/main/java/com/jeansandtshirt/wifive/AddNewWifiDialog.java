package com.jeansandtshirt.wifive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by samir on 2016-08-31.
 */
public class AddNewWifiDialog extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View addWifiDialog = inflater.inflate(R.layout.add_wifi_fragment, null);

        //Get EditText in Dialogs
        final EditText SSID = (EditText)addWifiDialog.findViewById(R.id.SSID);
        final EditText password = (EditText) addWifiDialog.findViewById(R.id.password);
        Boolean wifiSecured = new PhysicalWiFiData().wifiSecured(getActivity().getApplicationContext());

        if( wifiSecured == false){
            password.setEnabled(false);
            password.setHint(R.string.password_not_required);
        }
        else if (wifiSecured == true){
            password.setEnabled(true);
            password.setHint(R.string.password_hint);
        }
        else{
            builder.setMessage(R.string.fragment_error_message)
                    .setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            //Do stuff here
                        }
                    });
        }

        SSID.setText(new PhysicalWiFiData().getWifiName(getActivity().getApplicationContext()));
        SSID.setEnabled(false);

        builder.setView(addWifiDialog);
        builder.setTitle(R.string.add_wifi_title)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        if (password.isEnabled() && password.getText().toString().equals("")){
                            password.setError(getActivity().getApplicationContext().getString(R.string.enter_password));
                        }
                        else{
                            new WiFiveFirebase().addWifi(new WiFive().mRef, new WiFive().getmLastLocation(), getActivity(), password.getText().toString());
                        }
                        //Do stuff onClick
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //Do stuff onClick
                    }
                });

        return builder.create();
    }
}
