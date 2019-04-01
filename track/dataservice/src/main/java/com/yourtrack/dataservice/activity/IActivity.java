package com.yourtrack.dataservice.activity;

import java.util.Collection;
import java.util.List;

public interface IActivity {
    List<Point> getPoints();
    Point getFirstPoint();
    Point getLastPoint();
    int getPointCount();

    void addPoint(Point p);
    void setPoints(int startIndex, Collection<Point> points);
}
