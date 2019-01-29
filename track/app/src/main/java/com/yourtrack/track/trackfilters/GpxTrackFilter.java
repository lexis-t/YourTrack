package com.yourtrack.track.trackfilters;

import android.location.Location;

import com.yourtrack.track.map.ITrackFilter;
import com.yourtrack.track.map.TrackPatch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDateTime;

import java.io.File;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.TrackSegment;

public class GpxTrackFilter implements ITrackFilter {
    private String dir;

    public GpxTrackFilter(CharSequence dir) {
        this.dir = dir.toString();
    }

    @Override @NotNull
    public FilterResult filterTrack(TrackPatch track, @Nullable Location nextLocation, @NotNull FilterResult cumulativeResult) throws Exception{

        if (track.getPointCount() > 2) {
            LocalDateTime time = new LocalDateTime(track.getFirstPoint().getTime());
            File f = new File(dir, time.toString() + ".gpx");


            TrackSegment.Builder segmentBuilder = TrackSegment.builder();
            track.getPoints().forEach(p -> segmentBuilder.addPoint(ptBuilder -> ptBuilder.lat(p.getLatitude()).lon(p.getLongitude())));

            GPX gpx = GPX.builder().addTrack(t -> t.addSegment(segmentBuilder.build())).build();

            GPX.write(gpx, f.getPath());
        }

        return cumulativeResult;
    }
}
