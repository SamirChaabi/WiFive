package com.jeansandtshirt.wifive;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Created by samir on 2016-08-10.
 */
public class WifiAlreadyExistsDialog extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new WiFive().getActivity());
        builder.setTitle(R.string.wifi_exists_title)
                .setMessage(R.string.wifi_exists_message)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return builder.create();
    }
}
