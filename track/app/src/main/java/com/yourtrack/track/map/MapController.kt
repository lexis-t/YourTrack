/*
YourTrack
Copyright (C) 2018  Alexey Tikhvinskiy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.yourtrack.track.map

import android.os.Environment
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import com.yourtrack.track.R
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File


class MapController(context: Context, private val view: MapView, initialLocation : Location) {
    private val PREFERENCES_FILE = "mapcontroller.pref"
    private val NAME_POS = "position"

    private val pref : SharedPreferences
    private var location : LatLong? = null
    private val locationMarker : Marker

    init {
        pref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

        val locationBmp = AndroidGraphicFactory.convertToBitmap(context.getDrawable(R.drawable.ic_location_mark))
        locationMarker = Marker(LatLong(initialLocation.latitude, initialLocation.longitude), locationBmp, locationBmp.width/2, locationBmp.height/2)
        view.mapScaleBar.isVisible = true
        view.isClickable = true
        view.setBuiltInZoomControls(true)
        view.setZoomLevel(15)

        val tileCache = AndroidUtil.createTileCache(context, "mapcache",
               view.model.displayModel.tileSize, 1f,
               view.model.frameBufferModel.overdrawFactor)

        val mapDataStore = MapFile(File(Environment.getExternalStorageDirectory(), "earth.map"))
        val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore, view.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)

        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

        view.layerManager.layers.add(tileRendererLayer)
        view.layerManager.layers.add(locationMarker)

        val locationStr = pref.getString(NAME_POS, null)
        if (locationStr != null) {
            location = LatLong.fromString(locationStr)

            locationMarker.latLong = location
            view.setCenter(location)
        }
    }

    fun onLocation(newLocation: Location?) {
        if (newLocation != null) {
            val latLong = LatLong(newLocation.latitude, newLocation.longitude)
            if (location == null || location != view.boundingBox.centerPoint) {
                location = latLong
                locationMarker.latLong = location
                view.setCenter(location)
                pref.edit {  putString(NAME_POS, "${location!!.latitude}:${location!!.longitude}") }
            }
        }
    }

    fun centerLastLocation() {
        if (location != null) {
            view.setCenter(location)
        }
    }

    fun getMap(): MapView {
        return view;
    }

    fun newTrack(): TrackController {
        val track = TrackController(this)
        view.layerManager.layers.add(track.layer)
        return track
    }

    fun close() {
        view.destroyAll()
    }
}
