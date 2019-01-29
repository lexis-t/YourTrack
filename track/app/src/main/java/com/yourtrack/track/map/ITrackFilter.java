package com.yourtrack.track.map;

import android.location.Location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ITrackFilter {
    enum FilterResult {
        LAST_POINT, ALL_POINTS, DROP_UPDATE;

        public static FilterResult accumulate(FilterResult cumulativeResult, FilterResult result) {
            return cumulativeResult.ordinal() < result.ordinal() ? result : cumulativeResult;
        }
    }
    @NotNull FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NotNull FilterResult cumulativeResult) throws Exception;

}
