package org.setu.showcase.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import org.setu.showcase.databinding.ActivityProjectMapsBinding
import org.setu.showcase.databinding.ContentProjectMapsBinding
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.Location


class ProjectMapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityProjectMapsBinding
    private lateinit var contentBinding: ContentProjectMapsBinding
    lateinit var map: GoogleMap
    lateinit var app: MainApp
    var portfolioType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        binding = ActivityProjectMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        portfolioType = intent.getStringExtra("portfolio_type")!!
        if (portfolioType == "Show All") {
            binding.toolbar.title = "Map of All Projects"
        } else {
            binding.toolbar.title = "Map of $portfolioType"
        }
        setSupportActionBar(binding.toolbar)
        contentBinding = ContentProjectMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync {
            map = it
            configureMap()
        }




        println("this is maps portfolioType: $portfolioType")
    }

    fun configureMap() {
        map.setOnMarkerClickListener(this)
        map.uiSettings.setZoomControlsEnabled(true)
        println("this is findProjects: " + app.portfolios.findProjects())
        if (portfolioType == "Show All") {
            app.portfolios.findProjects().forEach {
                val loc = LatLng(it.lat, it.lng)
                val options = MarkerOptions().title(it.projectTitle).position(loc)
                map.addMarker(options)?.tag = it.projectId
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
            }
        } else {
            app.portfolios.findSpecificTypeProjects(portfolioType).forEach {
                val loc = LatLng(it.lat, it.lng)
                val options = MarkerOptions().title(it.projectTitle).position(loc)
                map.addMarker(options)?.tag = it.projectId
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
            }
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long
        val project = app.portfolios.findProject(tag)
        //contentBinding.currentTitle.text = marker.title
        println("this is project: $project")
        if (project != null) {
            contentBinding.currentTitle.text = project.projectTitle
            contentBinding.currentDescription.text = project.projectDescription
            contentBinding.currentPortfolio.text = "Portfolio: ${project.projectPortfolioName}"
            Picasso.get().load(project.projectImage).resize(200,200).into(contentBinding.currentImage)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }
}