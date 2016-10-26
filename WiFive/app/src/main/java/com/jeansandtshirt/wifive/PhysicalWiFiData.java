package com.jeansandtshirt.wifive;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by samir on 2016-07-29.
 */
public class PhysicalWiFiData {

    public String getWifiName(Context context) {
        WifiInfo wifiInfo = wifiCheck(context);
        if (wifiCheck(context) != null){
            return wifiInfo.getSSID();
        }
        return null;
    }

    public String getMAC(Context context) {
        WifiInfo wifiInfo = wifiCheck(context);
        if (wifiCheck(context) != null){
            Log.d("MAC", wifiInfo.getBSSID());
            return wifiInfo.getBSSID();
        }
        return null;
    }

    public WifiInfo wifiCheck(Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo;
                }
            }
        }
        return null;
    }

    public Boolean wifiSecured (Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = manager.getScanResults();

        //get current connected SSID for comparison to ScanResult
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String currentSSID = wifiInfo.getBSSID();

        if (networkList != null) {

            for (ScanResult network : networkList)
            {
                //check if current connected SSID
                if (currentSSID.equals(network.BSSID)){
                    //get capabilities of current connection
                    String Capabilities =  network.capabilities;
                    Log.d ("Wifi name:", network.SSID + " capabilities : " + Capabilities);

                    if (Capabilities.contains("WPA2")) {
                        return true;
                    }
                    else if (Capabilities.contains("WPA")) {
                        return true;
                    }
                    else if (Capabilities.contains("WEP")) {
                        return true;
                    }
                    //Open network
                    else
                        return false;
                }
            }
        }
        else
            return null;
        return  null;
    }
    public boolean isConnected(Context context){
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getActiveNetworkInfo();

        if(mWifi != null && mWifi.getTypeName().equals("WIFI"))
            return true;
        else
            return false;

    }
}
