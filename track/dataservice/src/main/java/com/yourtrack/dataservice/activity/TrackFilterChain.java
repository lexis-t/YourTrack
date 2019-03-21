package com.yourtrack.dataservice.activity;

import android.location.Location;

import java.util.Collection;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

public class TrackFilterChain implements ITrackFilter {
    private Collection<ITrackFilter> filters;

    public TrackFilterChain(Collection<ITrackFilter> filters) {
        this.filters = filters;
    }

    @Override @NonNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NonNull FilterResult cumulativeRes) throws Exception{
        FilterResult res = cumulativeRes;
        for (ITrackFilter f: filters) {
            res = f.filterTrack(track, nextLocation, res);
            if (res.ordinal() < cumulativeRes.ordinal()) {
                throw new IllegalStateException("Track filter misbehaves: " + f.getClass().getSimpleName());
            }
            if (res == FilterResult.DROP_UPDATE) {
                break;
            }
        }
        return cumulativeRes;
//        return filters.stream().map(f -> f.filterTrack(track, nextLocation)).max(Comparator.comparingInt(Enum::ordinal)).orElse(FilterResult.NO_UPDATE);
    }
}
