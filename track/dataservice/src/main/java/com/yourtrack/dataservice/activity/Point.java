package com.yourtrack.dataservice.activity;

import android.location.Location;
import android.os.Build;

public class Point {
    private long time;
    private double latitude;
    private double longitude;
    private int accuracy = -1;
    private int altitude;
    private int altitudeAccuracy = -1;
    private int hr;
    private int hrAccuracy = -1;

    public Point(long time) {
        this.time = time;
    }

    public Point(Location location) {
        this(location.getTime());
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        if (location.hasAccuracy()) {
            this.accuracy = (int)location.getAccuracy();
        }
        else {
            this.accuracy = 0;
        }
        if (location.hasAltitude()) {
            this.altitude = (int)location.getAltitude();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.altitudeAccuracy = (int)location.getVerticalAccuracyMeters();
            }
            else {
                this.altitudeAccuracy = 0;
            }
        }
        else {
            this.altitude = -1;
        }
    }

    public long getTime() {
        return time;
    }

    public boolean hasLocation() {
        return accuracy >= 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public boolean hasAltitude() {
        return altitudeAccuracy >= 0;
    }

    public int getAltitude() {
        return altitude;
    }

    public int getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public void setHeartRate(int hr, int hrAccuracy) {
        this.hr = hr;
        this.hrAccuracy = hrAccuracy;
    }

    public boolean hasHeartRate() {
        return hrAccuracy >= 0;
    }

    public int getHeartRate() {
        return hr;
    }

    public int getHeartRateAccuracy() {
        return hrAccuracy;
    }
}
