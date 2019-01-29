package com.yourtrack.track.trackfilters;

import android.location.Location;

import com.yourtrack.track.map.ITrackFilter;
import com.yourtrack.track.map.Point;
import com.yourtrack.track.map.TrackPatch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinMoveTrackFilter implements ITrackFilter {
    private double filterMetric;

    public MinMoveTrackFilter(double distance) {
        this.filterMetric = distance * distance;
    }

    @Override @NotNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location next, @NotNull FilterResult cumulativeResult) {
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
