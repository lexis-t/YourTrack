package com.yourtrack.track.map;

import android.location.Location;

import com.yourtrack.dataservice.activity.ITrackFilter;

import org.joda.time.Instant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ITrackController {
    void setTrackFilter(@NonNull ITrackFilter filter);
    void onLocation(@Nullable Location newLocation);
    Instant getStartTime();
    int getPointCount();
}
