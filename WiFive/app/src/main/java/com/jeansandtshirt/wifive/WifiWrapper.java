package com.jeansandtshirt.wifive;

/**
 * Created by samir on 2016-08-23.
 */
public class WifiWrapper {
    private WiFi wiFi;
    private String firebaseKey;

    public WifiWrapper(WiFi wiFi, String firebaseKey) {
        this.wiFi = wiFi;
        this.firebaseKey = firebaseKey;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public WiFi getWiFi() {
        return wiFi;
    }
}
