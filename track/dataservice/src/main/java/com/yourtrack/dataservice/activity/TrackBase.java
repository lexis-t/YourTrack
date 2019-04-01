package com.yourtrack.dataservice.activity;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TrackBase implements IActivity {
    private LinkedList<Point> points = new LinkedList<>();

    @Override
    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    @Override
    public Point getFirstPoint() {
        return points.size() > 0 ? points.getFirst() : null;
    }

    @Override
    public Point getLastPoint() {
        return points.size() > 0 ? points.getLast() : null;
    }

    @Override
    public int getPointCount() {
        return points.size();
    }

    @Override
    public void addPoint(Point p) {
        points.add(p);
    }

    public void setPoints(int startIndex, Collection<Point> points) {
        while (this.points.size() > startIndex) {
            this.points.removeLast();
        }
        this.points.addAll(points);
    }

    @Override
    public TrackBase clone() throws CloneNotSupportedException {
        TrackBase t = (TrackBase) super.clone();
        t.points = new LinkedList<>(points);
        return t;
    }
}
