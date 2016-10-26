package com.jeansandtshirt.wifive;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

/**
 * Created by samir on 2016-07-24.
 */
public class WiFi {
    public String SSID;
    public String password;
    public String MAC;
    public String deviceID;
    public double latitude;
    public double longitude;
    public String city;
    public HashMap<String, Double> userRatings;

    public WiFi(){
    }

    public WiFi(String SSID, String password, String MAC, String deviceID, double latitude, double longitude, String city, HashMap<String, Double> userRatings){
        this.SSID = SSID;
        this.password = password;
        this.MAC = MAC;
        this.deviceID = deviceID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.userRatings = userRatings;
    }

    @JsonProperty("SSID")
    public String getSSID() {
        return SSID;
    }

    @JsonProperty("SSID")
    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("MAC")
    public String getMAC() {
        return MAC;
    }

    @JsonProperty("MAC")
    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getDeviceID() { return deviceID; }

    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HashMap<String, Double> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(HashMap<String, Double> userRatings) {
        this.userRatings = userRatings;
    }
}