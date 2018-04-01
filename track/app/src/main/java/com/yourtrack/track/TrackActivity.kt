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

package com.yourtrack.track

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_track.*

import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.yourtrack.track.map.MapController


class TrackActivity : AppCompatActivity() {

    private val PREFS = "settings.pref"

    private var mapController: MapController? = null
    private var prefs : SharedPreferences? = null
    private val geoListener = object : LocationListener {
        override fun onLocationChanged(l: Location?) {
            if (mapController != null && l != null) {
                mapController!!.onLocation(l)
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_track)

        setSupportActionBar(toolbar)

        val actionbar = supportActionBar

        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        AndroidGraphicFactory.createInstance(application)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3F, geoListener)
            mapController = MapController(this, map, locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        }
        catch (e : SecurityException) {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        try {

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                mapController!!.onLocation(location)
            }
        }
        catch (e : SecurityException) {
        }
    }

    override fun onDestroy() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(geoListener)

        if (mapController != null) {
            mapController!!.close()
        }

        AndroidGraphicFactory.clearResourceMemoryCache()

        super.onDestroy()
    }
}
