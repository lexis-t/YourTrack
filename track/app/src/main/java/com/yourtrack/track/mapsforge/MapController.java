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

package com.yourtrack.track.mapsforge;

import android.os.Environment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.yourtrack.track.R;
import com.yourtrack.track.map.IMapController;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import java.io.File;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MapController implements IMapController {
    private final static String PREFERENCES_FILE =  "mapcontroller.pref";
    private final static String NAME_POS = "position";
    private final SharedPreferences pref;
    private LatLong location;
    private final Marker locationMarker;
    private final MapView view;

    public MapController(@NotNull Context context, @NotNull MapView view, @NotNull Location initialLocation) {
        super();
        this.view = view;
        this.pref = context.getSharedPreferences(PREFERENCES_FILE, 0);

        Bitmap locationBmp = AndroidGraphicFactory.convertToBitmap(context.getDrawable(R.drawable.ic_location_mark));
        this.locationMarker = new Marker(new LatLong(initialLocation.getLatitude(), initialLocation.getLongitude()), locationBmp, locationBmp.getWidth() / 2, locationBmp.getHeight() / 2);
        this.view.getMapScaleBar().setVisible(true);
        this.view.setClickable(true);
        this.view.setBuiltInZoomControls(true);
        this.view.setZoomLevel((byte)15);

        TileCache tileCache = AndroidUtil.createTileCache(context, "mapcache",
                view.getModel().displayModel.getTileSize(), 1.0F,
                view.getModel().frameBufferModel.getOverdrawFactor());

        MapFile mapDataStore = new MapFile(new File(Environment.getExternalStorageDirectory(), "earth.map"));
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, this.view.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

        Layers layers = view.getLayerManager().getLayers();
        layers.add(tileRendererLayer);
        layers.add(locationMarker);
        String locationStr = this.pref.getString(NAME_POS, null);
        if (locationStr != null) {
            this.location = LatLong.fromString(locationStr);
            this.locationMarker.setLatLong(this.location);
            this.view.setCenter(this.location);
        }
    }

    public final void onLocation(@Nullable Location newLocation) {
        if (newLocation != null) {
            LatLong latLong = new LatLong(newLocation.getLatitude(), newLocation.getLongitude());
            if (location == null || !location.equals(view.getBoundingBox().getCenterPoint())) {
                location = latLong;
                locationMarker.setLatLong(location);
                view.setCenter(location);

                pref.edit().putString(NAME_POS, "" + location.latitude + ':' + location.longitude).apply();
            }
        }

    }

    public final void centerLastLocation() {
        if (this.location != null) {
            this.view.setCenter(this.location);
        }

    }

    public final float getScale() {
        return view.getModel().displayModel.getScaleFactor();
    }

    @NotNull
    public final TrackController newTrack() {
        TrackController track = new TrackController(this);
        view.getLayerManager().getLayers().add(track.getLayer());
        return track;
    }

    void repaintMap() {
        view.postInvalidate();
    }

    public final void close() {
        view.destroyAll();
    }


}

