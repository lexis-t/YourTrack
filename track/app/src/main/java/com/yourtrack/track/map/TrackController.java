package com.yourtrack.track.map;

import android.location.Location;

import org.jetbrains.annotations.Nullable;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Polyline;

import java.lang.ref.WeakReference;

public class TrackController {
    private WeakReference<MapController> mapController;
    private Paint paint;
    private Polyline layer;
    TrackController(MapController map) {
        mapController = new WeakReference<>(map);
        paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN));
        paint.setStrokeWidth(2 * map.getScale());
        paint.setStyle(Style.STROKE);

        layer = new Polyline(paint, AndroidGraphicFactory.INSTANCE);
    }

    public void onLocation(@Nullable Location newLocation) {
        if (newLocation != null) {
            layer.addPoint(new LatLong(newLocation.getLatitude(), newLocation.getLongitude()));
            mapController.get().repaintMap();
        }
    }

    Layer getLayer() {
        return layer;
    }
}