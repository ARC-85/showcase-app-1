package org.setu.showcase.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.setu.showcase.R
import org.setu.showcase.models.Location
import org.setu.showcase.models.NewProject
import org.setu.showcase.models.PortfolioModel
import org.w3c.dom.Text

class MapActivity : AppCompatActivity(), OnMapReadyCallback,  GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    var location = Location()
    var project = NewProject()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Passing the details of the location
        location = intent.extras?.getParcelable<Location>("location")!!
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loc = LatLng(location.lat, location.lng)
        // Setting up the map marker to allow dragging, start in the right position, and show the co-ordinates
        val options = MarkerOptions()
            .title("Project")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerDragStart(marker: Marker) {

    }

    override fun onMarkerDrag(marker: Marker)  {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        // Updating co-ordinates after dragging
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Showing co-ordinates on marker click
        val loc = LatLng(location.lat, location.lng)
        println(loc)
        marker.snippet = "GPS : $loc"
        return false
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        // Passing the new location upon back click
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }


}