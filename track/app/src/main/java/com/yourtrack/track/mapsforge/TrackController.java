package com.yourtrack.track.mapsforge;

import android.location.Location;
import android.util.Log;

import com.yourtrack.dataservice.activity.ITrackFilter;
import com.yourtrack.dataservice.activity.Point;
import com.yourtrack.dataservice.activity.TrackBase;
import com.yourtrack.dataservice.activity.TrackPatch;
import com.yourtrack.track.map.ITrackController;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Instant;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Polyline;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TrackController implements ITrackController {

    private static final String LOGTAG = "ET-Track";

    private class Track extends TrackBase {
        @Override
        public void addPoint(Point p) {
            super.addPoint(p);
            layer.addPoint(new LatLong(p.getLatitude(), p.getLongitude()));
        }
        @Override
        public void setPoints(int startIndex, Collection<Point> points) {
            super.setPoints(startIndex, points);
            List<LatLong> layerPoints = layer.getLatLongs();
            while (layerPoints.size() > startIndex) {
                layerPoints.remove(layerPoints.size());
            }
            List<Point> newPoints = getPoints();
            layer.addPoints(newPoints.subList(startIndex,
                    newPoints.size()).stream()
                    .map(p -> new LatLong(p.getLatitude(), p.getLongitude()))
                    .collect(Collectors.toList()));
        }
    }

    private WeakReference<MapController> mapController;
    private Paint paint;
    private Polyline layer;
    private Track track;
    private ITrackFilter filter;

    TrackController(MapController map) {
        mapController = new WeakReference<>(map);
        paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN));
        paint.setStrokeWidth(2 * map.getScale());
        paint.setStyle(Style.STROKE);

        layer = new Polyline(paint, AndroidGraphicFactory.INSTANCE);
        track = new Track();
    }

    public void setTrackFilter(@NotNull ITrackFilter filter) {
        this.filter = filter;
    }

    public void onLocation(@Nullable Location nextLocation) {
        try {
            if (filter != null) {
                TrackPatch patch = new TrackPatch(track);
                patch.addPoint(new Point(nextLocation));
                if (filter.filterTrack(patch, nextLocation, ITrackFilter.FilterResult.LAST_POINT) != ITrackFilter.FilterResult.DROP_UPDATE) {
                    patch.applyPatch();
                    mapController.get().repaintMap();
                }
            } else {
                track.addPoint(new Point(nextLocation));
                mapController.get().repaintMap();
            }
        }
        catch (Exception e) {
            Log.e(LOGTAG, "Location update is failed", e);
        }
    }

    Layer getLayer() {
        return layer;
    }

    public Instant getStartTime() {
        Point start = track.getFirstPoint();
        return start != null ? new Instant(start.getTime()) : null;
    }

    public int getPointCount() {
        return track.getPointCount();
    }

}