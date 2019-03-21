package com.yourtrack.dataservice.activity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ITrackFilter {
    enum FilterResult {
        LAST_POINT, ALL_POINTS, DROP_UPDATE;

        public static FilterResult accumulate(FilterResult cumulativeResult, FilterResult result) {
            return cumulativeResult.ordinal() < result.ordinal() ? result : cumulativeResult;
        }
    }
    @NonNull FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NonNull FilterResult cumulativeResult) throws Exception;

}
