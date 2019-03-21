package com.yourtrack.dataservice.activity;

import android.location.Location;

public class Point {
    private double latitude;
    private double longitude;
    private double altitude;
    private long time;

    public Point(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        time = location.getTime();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTime() {
        return time;
    }
}
