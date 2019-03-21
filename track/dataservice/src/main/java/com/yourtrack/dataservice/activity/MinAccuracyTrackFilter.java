package com.yourtrack.dataservice.activity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MinAccuracyTrackFilter implements ITrackFilter {
    private double accuracy;

    public MinAccuracyTrackFilter(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override @NonNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NonNull FilterResult cumulativeResult) {
        if (nextLocation == null || !nextLocation.hasAccuracy() || nextLocation.getAccuracy() > accuracy) {
            cumulativeResult = FilterResult.DROP_UPDATE;
        }
        else {
            cumulativeResult = FilterResult.accumulate(cumulativeResult, FilterResult.LAST_POINT);
        }
        return cumulativeResult;
    }
}
