package com.jeansandtshirt.wifive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by samir on 2016-08-08.
 */
public class WifiNotConnectedDialog extends android.support.v4.app.DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.fragment_error_message)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //Do stuff here
                    }
                });

        return builder.create();
    }
}
