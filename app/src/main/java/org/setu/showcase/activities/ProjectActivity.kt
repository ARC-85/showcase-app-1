package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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
    private lateinit var image2IntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var image3IntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var project = NewProject()
    var edit = false
    val today = Calendar.getInstance()
    var dateDay = today.get(Calendar.DAY_OF_MONTH)
    var dateMonth = today.get(Calendar.MONTH)
    var dateYear = today.get(Calendar.YEAR)
    val projectBudgets = arrayOf("€0-€50K", "€50K-€100K", "€100K-€250K", "€250K-€500K", "€500K-€1M", "€1M+")
    var projectBudget = ""

    var portfolio = PortfolioModel()

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
        binding.projectName.isVisible = false
        binding.chooseImage2.isVisible = false
        binding.projectImage2.isVisible = false
        binding.chooseImage3.isVisible = false
        binding.projectImage3.isVisible = false

        app = application as MainApp

        if (!intent.hasExtra("location")) {
            binding.projectLatitude.isVisible = true
            binding.projectLongitude.isVisible = true
        }

        if (intent.hasExtra("project_edit")) {
            edit = true
            project = intent.extras?.getParcelable("project_edit")!!
            binding.projectName.text = project.projectTitle
            binding.projectName.isVisible = true
            binding.newProjectLabel.isVisible = false
            binding.projectTitle.setText(project.projectTitle)
            if (!intent.hasExtra("location")) {
                binding.projectDescription.setText(project.projectDescription)
                var formattedLatitude = String.format("%.2f", project.lat);
                binding.projectLatitude.setText("Latitude: $formattedLatitude")
                var formattedLongitude = String.format("%.2f", project.lng);
                binding.projectLongitude.setText("Longitude: $formattedLongitude")
            }
            /*binding.projectDescription.setText(project.projectDescription)
            var formattedLatitude = String.format("%.2f", project.lat);
            binding.projectLatitude.setText(formattedLatitude)
            var formattedLongitude = String.format("%.2f", project.lng);
            binding.projectLongitude.setText(formattedLongitude)*/
            binding.btnProjectAdd.setText(R.string.save_project)
            binding.btnProjectAdd.isVisible = false
            binding.projectLatitude.isVisible = true
            binding.projectLongitude.isVisible = true

            Picasso.get()
                .load(project.projectImage)
                .centerCrop()
                .resize(450, 420)
                .into(binding.projectImage)
            if (project.projectImage != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.button_changeImage)
            }

            Picasso.get()
                .load(project.projectImage2)
                .centerCrop()
                .resize(450, 420)
                .into(binding.projectImage2)
            if (project.projectImage2 != Uri.EMPTY) {
                binding.chooseImage2.isVisible = true
                binding.projectImage2.isVisible = true
                binding.chooseImage2.setText(R.string.button_changeImage)
            }

            Picasso.get()
                .load(project.projectImage3)
                .centerCrop()
                .resize(450, 420)
                .into(binding.projectImage3)
            if (project.projectImage3 != Uri.EMPTY) {
                binding.chooseImage3.isVisible = true
                binding.projectImage3.isVisible = true
                binding.chooseImage3.setText(R.string.button_changeImage)
            }

        }

        binding.btnProjectAdd.setOnClickListener() {
            project.projectTitle = binding.projectTitle.text.toString()
            project.projectDescription = binding.projectDescription.text.toString()
            project.projectCompletionDay = dateDay
            project.projectCompletionMonth = dateMonth
            project.projectCompletionYear = dateYear
            project.projectBudget = projectBudget
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
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("project_addition", portfolio)
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
            binding.chooseImage2.isVisible = true
            binding.projectImage2.isVisible = true
        }

        binding.chooseImage2.setOnClickListener {
            showImagePicker(image2IntentLauncher)
            binding.chooseImage3.isVisible = true
            binding.projectImage3.isVisible = true
        }

        binding.chooseImage3.setOnClickListener {
            showImagePicker(image3IntentLauncher)
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

        val spinner = findViewById<Spinner>(R.id.projectBudgetSpinner)

        if (edit) {
            projectBudget = project.projectBudget
        }

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, projectBudgets)
            spinner.adapter = adapter
            if (projectBudget != null) {
                val spinnerPosition = adapter.getPosition(projectBudget)
                spinner.setSelection(spinnerPosition)
            }

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    projectBudget = projectBudgets[position]
                    Toast.makeText(this@ProjectActivity,
                        getString(R.string.selected_item) + " " +
                                "" + projectBudgets[position], Toast.LENGTH_SHORT).show()
                    projectBudget = projectBudgets[position]
                    println("this is portfolioType: $projectBudget")
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
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
                val intent = Intent(this, ProjectListActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
            R.id.item_project_delete -> {
                if (intent.hasExtra("project_edit")) {
                    project = intent.extras?.getParcelable("project_edit")!!
                    println("this is the delete project: $project")
                    portfolio =  app.portfolios.findPortfolio(portfolio)!!
                    app.portfolios.deleteProject(project, portfolio)
                    val intent = Intent(this, ProjectListActivity::class.java)
                    intent.putExtra("project_addition", portfolio)
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
                project.projectBudget = projectBudget

                if (project.projectTitle.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content),R.string.enter_project_title, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (edit) {
                        app.portfolios.updateProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        val intent = Intent(this, ProjectListActivity::class.java)
                        intent.putExtra("project_addition", portfolio)
                        startActivity(intent)
                    } else {
                        app.portfolios.createProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        val intent = Intent(this, ProjectListActivity::class.java)
                        intent.putExtra("project_addition", portfolio)
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
                                .centerCrop()
                                .resize(450, 420)
                                .into(binding.projectImage)
                            binding.chooseImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
        image2IntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            project.projectImage2 = result.data!!.data!!
                            Picasso.get()
                                .load(project.projectImage2)
                                .centerCrop()
                                .resize(450, 420)
                                .into(binding.projectImage2)
                            binding.chooseImage2.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
        image3IntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            project.projectImage3 = result.data!!.data!!
                            Picasso.get()
                                .load(project.projectImage3)
                                .centerCrop()
                                .resize(450, 420)
                                .into(binding.projectImage3)
                            binding.chooseImage3.setText(R.string.button_changeImage)
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
                            var formattedLatitude = String.format("%.2f", location.lat);
                            binding.projectLatitude.setText("Latitude: $formattedLatitude")
                            var formattedLongitude = String.format("%.2f", location.lng);
                            binding.projectLongitude.setText("Longitude: $formattedLongitude")
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }




}


