package com.yourtrack.track.map;

import android.location.Location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMapController {
    void onLocation(@Nullable Location newLocation);
    void centerLastLocation();
    float getScale();
    @NotNull ITrackController newTrack();
    void close();
}
