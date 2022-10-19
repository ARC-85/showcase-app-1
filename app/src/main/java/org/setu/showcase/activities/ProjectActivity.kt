package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.setu.showcase.R
import org.setu.showcase.databinding.ActivityProjectBinding
import org.setu.showcase.helpers.showImagePicker
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.NewProject
import org.setu.showcase.models.PortfolioModel
import org.setu.showcase.models.Location
import timber.log.Timber.i
import java.util.Objects.toString




class ProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var project = NewProject()


    //val portfolioId=intent.getLongExtra("portfolio_Id", 0)
    var portfolio = PortfolioModel()
    //var location = Location(52.245696, -7.139102, 15f)
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false

        portfolio = intent.extras?.getParcelable("portfolio_edit")!!
        registerImagePickerCallback()

        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        binding.btnProjectDelete.isVisible = false
        binding.projectLatitude.isVisible = false
        binding.projectLongitude.isVisible = false
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

        if (intent.hasExtra("project_edit")) {
            edit = true
            project = intent.extras?.getParcelable("project_edit")!!
            binding.projectTitle.setText(project.projectTitle)
            binding.projectDescription.setText(project.projectDescription)
            var formattedLatitude = String.format("%.2f", project.lat);
            binding.projectLatitude.setText(formattedLatitude)
            var formattedLongitude = String.format("%.2f", project.lng);
            binding.projectLongitude.setText(formattedLongitude)
            binding.btnProjectAdd.setText(R.string.save_project)
            binding.btnProjectDelete.isVisible = true
            binding.projectLatitude.isVisible = true
            binding.projectLongitude.isVisible = true

            Picasso.get()
                .load(project.projectImage)
                .into(binding.projectImage)
            if (project.projectImage != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.button_changeImage)
            }

        }

        binding.btnProjectAdd.setOnClickListener() {
            project.projectTitle = binding.projectTitle.text.toString()
            project.projectDescription = binding.projectDescription.text.toString()
            project.portfolioId = portfolio.id
            if (project.projectTitle.isEmpty()) {
                Snackbar.make(it,R.string.enter_project_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.portfolios.updateProject(project.copy())
                } else {
                    app.portfolios.createProject(project.copy())
                }
            }
            setResult(RESULT_OK)

            val intent = Intent(this, PortfolioActivity::class.java)
            intent.putExtra("portfolio_edit", portfolio)
            startActivity(intent)


        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.projectLocation.setOnClickListener {
            i ("Set Location Pressed")
        }

        binding.btnProjectDelete.setOnClickListener() {
            app.portfolios.deleteProject(project)
            val intent = Intent(this, PortfolioActivity::class.java)
            intent.putExtra("portfolio_edit", portfolio)
            startActivity(intent)
        }

        binding.projectLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (project.zoom != 0f) {
                location.lat =  project.lat
                location.lng = project.lng
                location.zoom = project.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
                .putExtra("project_edit", project)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerMapCallback()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            project.projectImage = result.data!!.data!!
                            Picasso.get()
                                .load(project.projectImage)
                                .into(binding.projectImage)
                            binding.chooseImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            project.lat = location.lat
                            project.lng = location.lng
                            project.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }




}


