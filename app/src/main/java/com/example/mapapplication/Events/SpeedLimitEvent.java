package com.example.mapapplication.Events;

public class SpeedLimitEvent {
    public double latitude;
    public double longitude;
    public float speed;
    public long timestamp;

    public SpeedLimitEvent() {
    }

    public SpeedLimitEvent(double latitude, double longitude, float speed, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }
}