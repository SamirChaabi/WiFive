package com.jeansandtshirt.wifive;

/**
 * Created by samir on 2016-08-13.
 */
public class Rating {

    public String deviceID;
    public int rating;

    public Rating(){

    }

    public Rating(String deviceID, int rating){
        this.deviceID = deviceID;
        this.rating = rating;
    }

    public String getDeviceID() { return deviceID; }

    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public double getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }
}
