package com.yourtrack.track.map;

import android.location.Location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Instant;

public interface ITrackController {
    void setTrackFilter(@NotNull ITrackFilter filter);
    void onLocation(@Nullable Location newLocation);
    Instant getStartTime();
    int getPointCount();
}
