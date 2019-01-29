package com.yourtrack.track.trackfilters;

import android.location.Location;

import com.yourtrack.track.map.ITrackFilter;
import com.yourtrack.track.map.TrackPatch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinAccuracyTrackFilter implements ITrackFilter {
    private double accuracy;

    public MinAccuracyTrackFilter(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override @NotNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NotNull FilterResult cumulativeResult) {
        if (nextLocation == null || !nextLocation.hasAccuracy() || nextLocation.getAccuracy() > accuracy) {
            cumulativeResult = FilterResult.DROP_UPDATE;
        }
        else {
            cumulativeResult = FilterResult.accumulate(cumulativeResult, FilterResult.LAST_POINT);
        }
        return cumulativeResult;
    }
}
