package com.yourtrack.track.trackfilters;

import android.location.Location;

import com.yourtrack.track.map.ITrackFilter;
import com.yourtrack.track.map.TrackPatch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TrackFilterChain implements ITrackFilter {
    private Collection<ITrackFilter> filters;

    public TrackFilterChain(Collection<ITrackFilter> filters) {
        this.filters = filters;
    }

    @Override @NotNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NotNull FilterResult cumulativeRes) throws Exception{
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
