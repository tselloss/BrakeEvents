package com.example.mapapplication.Events;

public class PotholeEvent {
    public double latitude;
    public double longitude;
    public long timestamp;

    public PotholeEvent() {
    }

    public PotholeEvent(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
