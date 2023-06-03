package com.example.mapapplication;

public class BrakeEvent {
    public double latitude;
    public double longitude;
    public float speed;
    public long timestamp;

    public BrakeEvent() {
    }

    public BrakeEvent(double latitude, double longitude, float speed, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }
}

