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

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.activity_track.*

import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.yourtrack.track.map.ITrackFilter
import com.yourtrack.track.mapsforge.MapController
import com.yourtrack.track.mapsforge.TrackController
import com.yourtrack.track.trackfilters.GpxTrackFilter
import com.yourtrack.track.trackfilters.MinAccuracyTrackFilter
import com.yourtrack.track.trackfilters.MinMoveTrackFilter
import com.yourtrack.track.trackfilters.TrackFilterChain
import java.util.ArrayList


class TrackActivity : AppCompatActivity() {

    private val PREFS = "settings.pref"

    private var mapController: MapController? = null
    private var activeTrack: TrackController? = null
    private val trackFilter: TrackFilterChain
    private var prefs : SharedPreferences? = null
    private val geoListener = object : LocationListener {
        override fun onLocationChanged(l: Location?) {
            if (mapController != null && l != null) {
                mapController!!.onLocation(l)
            }
            if (activeTrack != null && l != null) {
                activeTrack!!.onLocation(l)
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

    init {
        val filters = ArrayList<ITrackFilter>()
        filters.add(MinAccuracyTrackFilter(30.0))
        filters.add(MinMoveTrackFilter(10.0))
        filters.add(GpxTrackFilter(Environment.getExternalStorageDirectory().absolutePath))

        trackFilter = TrackFilterChain(filters)
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

        val permissions = ArrayList<String>(2)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 0)
        }
        else {
            startTracking()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.size == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            startTracking()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }


        override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_current_location -> {
                mapController!!.centerLastLocation()
                return true
            }
            R.id.action_track -> {
                activeTrack = mapController!!.newTrack()
                activeTrack!!.setTrackFilter(trackFilter)
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

    private fun startTracking() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 150F, geoListener)
            var lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation == null) {
                lastLocation = Location("?")
                lastLocation.setLatitude(0.0)
                lastLocation.setLongitude(0.0)
            }
            mapController = MapController(this, map, lastLocation)
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
