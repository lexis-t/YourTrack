package com.yourtrack.track.map;

import android.location.Location;

import java.util.Collection;
import java.util.List;

public interface ITrack {
    List<Point> getPoints();
    Point getFirstPoint();
    Point getLastPoint();
    int getPointCount();

    void addPoint(Location nextLocation);
    void setPoints(int startIndex, Collection<Point> points);
}
