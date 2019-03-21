package com.yourtrack.track.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IMapController {
    void onLocation(@Nullable Location newLocation);
    void centerLastLocation();
    float getScale();
    @NonNull
    ITrackController newTrack();
    void close();
}
