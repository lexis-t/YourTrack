package com.yourtrack.track.map

import android.location.Location
import org.mapsforge.core.graphics.Color
import org.mapsforge.core.graphics.Paint
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Polyline

class TrackController(map: MapController) {
    private val paint: Paint
    val layer: Polyline
    init {
        paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.color = AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN)
        paint.strokeWidth = 2 * map.getMap().model.displayModel.scaleFactor
        paint.setStyle(Style.STROKE)

        layer = Polyline(paint, AndroidGraphicFactory.INSTANCE)
    }

    fun onLocation(newLocation: Location?) {
        if (newLocation != null) {
            layer.latLongs.add(LatLong(newLocation.latitude, newLocation.longitude))
        }
    }
}