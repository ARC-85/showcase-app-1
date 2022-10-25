package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
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
import java.util.*
import java.util.Objects.toString




class ProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var project = NewProject()
    var edit = false
    val today = Calendar.getInstance()
    var dateDay = today.get(Calendar.DAY_OF_MONTH)
    var dateMonth = today.get(Calendar.MONTH)
    var dateYear = today.get(Calendar.YEAR)


    //val portfolioId=intent.getLongExtra("portfolio_Id", 0)
    var portfolio = PortfolioModel()
    //var location = Location(52.245696, -7.139102, 15f)
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        portfolio = intent.extras?.getParcelable("portfolio_edit")!!
        registerImagePickerCallback()

        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarProject.title = "Project"
        binding.projectLatitude.isVisible = false
        binding.projectLongitude.isVisible = false
        setSupportActionBar(binding.toolbarProject)
        binding.btnProjectAdd.isVisible = false


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
            binding.btnProjectAdd.isVisible = false
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
            project.projectCompletionDay = dateDay
            project.projectCompletionMonth = dateMonth
            project.projectCompletionYear = dateYear
            project.portfolioId = portfolio.id

            if (project.projectTitle.isEmpty()) {
                Snackbar.make(it,R.string.enter_project_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.portfolios.updateProject(project.copy(), portfolio)
                    /*if (portfolioProjects != null) {
                        portfolioProjects += (project.copy())
                        portfolio.projects = portfolioProjects
                    }
                    app.portfolios.update(portfolio.copy())*/
                } else {

                    app.portfolios.createProject(project.copy(), portfolio)


                }
            }
            setResult(RESULT_OK)
            //finish()
            val intent = Intent(this, PortfolioListActivity::class.java)

            startActivity(intent)
            /*val intent = Intent(this, PortfolioActivity::class.java)
            intent.putExtra("portfolio_edit", portfolio)
            startActivity(intent)*/
            /*val launcherIntent = Intent(this, PortfolioActivity::class.java)
            launcherIntent.putExtra("portfolio_edit", portfolio)
            refreshIntentLauncher.launch(launcherIntent)*/


        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.projectLocation.setOnClickListener {
            i ("Set Location Pressed")
        }

        /*binding.btnProjectDelete.setOnClickListener() {
            app.portfolios.deleteProject(project, portfolio)
            val intent = Intent(this, PortfolioListActivity::class.java)

            startActivity(intent)
        }*/

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

        val datePicker = findViewById<DatePicker>(R.id.projectCompletionDatePicker)

        if (edit) {
            dateDay = project.projectCompletionDay
            dateMonth = project.projectCompletionMonth - 1
            dateYear = project.projectCompletionYear
        }
        datePicker.init(dateYear, dateMonth, dateDay) { view, year, month, day ->
            val month = month + 1
            val msg = "You Selected: $day/$month/$year"
            var dateProjectCompletion = "$day/$month/$year"
            dateDay = day
            dateMonth = month
            dateYear = year
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            println ("this is dateDay: $dateDay")
            println ("this is dateMonth: $dateMonth")
            println ("this is dateYear: $dateYear")
            println("this is datePicker: $datePicker")
            println("this is dateProjectCompletion: $dateProjectCompletion")
        }

        registerMapCallback()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        if (!edit) {
            menu.getItem(2).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
            R.id.item_project_delete -> {
                if (intent.hasExtra("project_edit")) {
                    project = intent.extras?.getParcelable("project_edit")!!
                    println("this is the delete project: $project")
                    app.portfolios.deleteProject(project, portfolio)
                    val intent = Intent(this, PortfolioListActivity::class.java)
                    startActivity(intent)
                }

            }
            R.id.item_project_save -> {
                project.projectTitle = binding.projectTitle.text.toString()
                project.projectDescription = binding.projectDescription.text.toString()
                project.projectCompletionDay = dateDay
                project.projectCompletionMonth = dateMonth
                project.projectCompletionYear = dateYear
                project.portfolioId = portfolio.id

                if (project.projectTitle.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content),R.string.enter_project_title, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (edit) {
                        app.portfolios.updateProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        val intent = Intent(this, PortfolioListActivity::class.java)
                        startActivity(intent)
                    } else {
                        app.portfolios.createProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        val intent = Intent(this, PortfolioListActivity::class.java)
                        startActivity(intent)
                    }
                }
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


