package com.yourtrack.dataservice.activity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MinMoveTrackFilter implements ITrackFilter {
    private double filterMetric;

    public MinMoveTrackFilter(double distance) {
        this.filterMetric = distance * distance;
    }

    @Override @NonNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location next, @NonNull FilterResult cumulativeResult) {
        if (next != null) {
            if (track.getFirstPatchIndex() == 0) {
                cumulativeResult = FilterResult.accumulate(cumulativeResult, FilterResult.LAST_POINT);
            }
            else {
                Point last = track.getBaseTrack().getLastPoint();
                if ((Math.pow(next.getLatitude() - last.getLatitude(), 2) + Math.pow(next.getLongitude() - last.getLongitude(), 2)) >= filterMetric) {
                    cumulativeResult = FilterResult.accumulate(cumulativeResult, FilterResult.LAST_POINT);
                }
            }
        }
        else {
            cumulativeResult = FilterResult.DROP_UPDATE;
        }
        return cumulativeResult;
    }
}
