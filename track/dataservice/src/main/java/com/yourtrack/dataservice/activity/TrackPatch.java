package com.yourtrack.dataservice.activity;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;

public class TrackPatch implements ITrack {
    private ITrack baseTrack;
    private int firstPatchIndex;
    private ArrayList<Point> patchPoints;

    public TrackPatch(@NonNull ITrack baseTrack) {
        this.baseTrack = baseTrack;
        this.firstPatchIndex = baseTrack.getPointCount();
    }


    @Override
    public List<Point> getPoints() {
        ArrayList<Point> points = new ArrayList<>(firstPatchIndex + (patchPoints != null ? patchPoints.size() : 0));
        points.stream().limit(firstPatchIndex).forEachOrdered(points::add);
        if (patchPoints != null) {
            points.addAll(patchPoints);
        }
        return points;
    }

    @Override
    public Point getFirstPoint() {
        Point p = null;
        if (patchPoints != null && firstPatchIndex == 0) {
            p = patchPoints.get(0);
        }
        else {
            p = baseTrack.getFirstPoint();
        }
        return p;
    }

    @Override
    public Point getLastPoint() {
        return (patchPoints != null && patchPoints.size() > 0) ? patchPoints.get(patchPoints.size() - 1) : baseTrack.getLastPoint();
    }

    @Override
    public int getPointCount() {
        return firstPatchIndex + (patchPoints != null ? patchPoints.size() : 0);
    }

    @Override
    public void addPoint(Location nextLocation) {
        if (patchPoints == null) {
            patchPoints = new ArrayList<>(1);
        }
        patchPoints.add(new Point(nextLocation));
    }

    @Override
    public void setPoints(int startIndex, Collection<Point> points) {
        if (startIndex > getPointCount()) {
            throw new IllegalArgumentException("Patch index is out of track bounds");
        }

        if (patchPoints == null) {
            patchPoints = new ArrayList<>(points.size());
        }
        Iterator<Point> patchIt = points.iterator();

        int i = startIndex;
        ListIterator<Point> it = patchPoints.listIterator();
        while (i < firstPatchIndex && patchIt.hasNext()) {
            it.add(patchIt.next());
            it.next();
            ++i;
        }

        while (it.hasNext() && patchIt.hasNext()) {
            it.set(patchIt.next());
            it.next();
        }

        while (patchIt.hasNext()) {
            patchPoints.add(patchIt.next());
        }

        firstPatchIndex = startIndex;
    }

    public ITrack getBaseTrack() {
        return baseTrack;
    }

    public int getFirstPatchIndex() {
        return firstPatchIndex;
    }

    public ArrayList<Point> getPatchPoints() {
        return patchPoints;
    }

    public void applyPatch() {
        if (patchPoints != null) {
            baseTrack.setPoints(firstPatchIndex, patchPoints);
        }
    }
}
