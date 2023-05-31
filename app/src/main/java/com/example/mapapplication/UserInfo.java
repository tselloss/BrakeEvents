package com.example.mapapplication;

public class UserInfo {

    private double longitude;
    private double latitude;

    public UserInfo() {
        // Default constructor required for Firebase Realtime Database
    }

    public UserInfo(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
