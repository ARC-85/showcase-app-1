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
    var edit = false // Used to show if editing/existing or new project addition
    // Variables used for setting initial DatePicker date to current date
    val today = Calendar.getInstance()
    var dateDay = today.get(Calendar.DAY_OF_MONTH)
    var dateMonth = today.get(Calendar.MONTH)
    var dateYear = today.get(Calendar.YEAR)
    val projectBudgets = arrayOf("€0-€50K", "€50K-€100K", "€100K-€250K", "€250K-€500K", "€500K-€1M", "€1M+") // Creating array of different project budgets
    var projectBudget = ""
    var portfolio = PortfolioModel()

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        portfolio = intent.extras?.getParcelable("portfolio_edit")!! // Attributing project to particular portfolio
        registerImagePickerCallback()
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarProject.title = "Project" // Helping to signpost in toolbar
        binding.projectLatitude.isVisible = false // Initially hidden until set
        binding.projectLongitude.isVisible = false // Initially hidden until set
        setSupportActionBar(binding.toolbarProject)
        binding.btnProjectAdd.isVisible = false // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future
        binding.projectName.isVisible = false // Initially hidden until set
        binding.chooseImage2.isVisible = false // Initially hidden until previous image set
        binding.projectImage2.isVisible = false // Initially hidden until previous image set
        binding.chooseImage3.isVisible = false // Initially hidden until previous image set
        binding.projectImage3.isVisible = false // Initially hidden until previous image set

        app = application as MainApp

        // Show co-ordinates if there is a location passed from the map
        if (!intent.hasExtra("location")) {
            binding.projectLatitude.isVisible = true
            binding.projectLongitude.isVisible = true
        }

        // If project already exists, set the initial values
        if (intent.hasExtra("project_edit")) {
            edit = true
            project = intent.extras?.getParcelable("project_edit")!!
            binding.projectName.text = project.projectTitle
            binding.projectName.isVisible = true
            binding.projectTitle.setText(project.projectTitle)
            binding.projectDescription.setText(project.projectDescription)
            // Set co-ordinates if there is a location passed from the map
            if (!intent.hasExtra("location")) {
                var formattedLatitude = String.format("%.2f", project.lat); // Limit the decimal places to two
                binding.projectLatitude.setText("Latitude: $formattedLatitude")
                var formattedLongitude = String.format("%.2f", project.lng);
                binding.projectLongitude.setText("Longitude: $formattedLongitude") // Limit the decimal places to two
            }
            binding.btnProjectAdd.setText(R.string.save_project) // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future
            binding.btnProjectAdd.isVisible = false // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future
            // Show co-ordinates if project exists
            binding.projectLatitude.isVisible = true
            binding.projectLongitude.isVisible = true
            // Use Picasso to load images and set their size with cropping
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

        // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future
        /*binding.btnProjectAdd.setOnClickListener() {
            project.projectTitle = binding.projectTitle.text.toString()
            project.projectDescription = binding.projectDescription.text.toString()
            project.projectCompletionDay = dateDay
            project.projectCompletionMonth = dateMonth
            project.projectCompletionYear = dateYear
            project.projectBudget = projectBudget
            project.portfolioId = portfolio.id
            project.projectPortfolioName = portfolio.title
            if (project.projectTitle.isEmpty()) {
                Snackbar.make(it,R.string.enter_project_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.portfolios.updateProject(project.copy(), portfolio)
                } else {

                    app.portfolios.createProject(project.copy(), portfolio)
                }
            }
            setResult(RESULT_OK)
            //finish()
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("project_addition", portfolio)
            startActivity(intent)
        }*/

        //Each time an image is selected, the option of selecting a subsequent image is shown
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

        // Set the initial values for location if a new location is set, passing details of location and project to the map activity
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

        // Set up DatePicker
        val datePicker = findViewById<DatePicker>(R.id.projectCompletionDatePicker)
        // Set initial values if a completion date already exists
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
            // Toast is turned off, but can be turned back on
            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            println ("this is dateDay: $dateDay")
            println ("this is dateMonth: $dateMonth")
            println ("this is dateYear: $dateYear")
            println("this is datePicker: $datePicker")
            println("this is dateProjectCompletion: $dateProjectCompletion")
        }

        // Set up spinner for setting project budget
        val spinner = findViewById<Spinner>(R.id.projectBudgetSpinner)
        // If a budget already exists, set initial value to spinner
        if (edit) {
            projectBudget = project.projectBudget
        }
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, projectBudgets)
            spinner.adapter = adapter
            // If a budget already exists, set initial value to spinner
            if (projectBudget != null) {
                val spinnerPosition = adapter.getPosition(projectBudget)
                spinner.setSelection(spinnerPosition)
            }
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    projectBudget = projectBudgets[position] // Index of array and spinner position used to select portfolio type
                    // The toast message was taken out because it was annoying, but can be reinstated if wanted
                    /*Toast.makeText(this@ProjectActivity,
                        getString(R.string.selected_item) + " " +
                                "" + projectBudgets[position], Toast.LENGTH_SHORT).show()*/
                    println("this is portfolioType: $projectBudget")
                }
                // No problem if nothing selected
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        // If project is new, delete option on menu is not shown
        if (!edit) {
            menu.getItem(3).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Item allowing to go back to origin portfolio
            R.id.item_cancel -> {
                val intent = Intent(this, ProjectListActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
            // Item allowing deletion of project
            R.id.item_project_delete -> {
                if (intent.hasExtra("project_edit")) {
                    project = intent.extras?.getParcelable("project_edit")!!
                    println("this is the delete project: $project")
                    portfolio =  app.portfolios.findPortfolio(portfolio)!!
                    // Delete project function called, passing both project and portfolio
                    app.portfolios.deleteProject(project, portfolio)
                    // Return to project list, passing portfolio type so that appropriate projects are listed
                    val intent = Intent(this, ProjectListActivity::class.java)
                    intent.putExtra("project_addition", portfolio)
                    startActivity(intent)
                }

            }
            // Item allowing addition/saving of a project, passing inputted values
            R.id.item_project_save -> {
                project.projectTitle = binding.projectTitle.text.toString()
                project.projectDescription = binding.projectDescription.text.toString()
                project.projectCompletionDay = dateDay
                project.projectCompletionMonth = dateMonth
                project.projectCompletionYear = dateYear
                project.portfolioId = portfolio.id
                project.projectBudget = projectBudget
                project.projectPortfolioName = portfolio.title
                // As a minimum, the project title is required, so a snackbar message is displayed if missing
                if (project.projectTitle.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content),R.string.enter_project_title, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (edit) { // If editing/existing, update is called
                        app.portfolios.updateProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        // Return to project list, passing portfolio type so that appropriate projects are listed
                        val intent = Intent(this, ProjectListActivity::class.java)
                        intent.putExtra("project_addition", portfolio)
                        startActivity(intent)
                    } else { // If new portfolio, create is called
                        app.portfolios.createProject(project.copy(), portfolio)
                        setResult(RESULT_OK)
                        // Return to project list, passing portfolio type so that appropriate projects are listed
                        val intent = Intent(this, ProjectListActivity::class.java)
                        intent.putExtra("project_addition", portfolio)
                        startActivity(intent)
                    }
                }
            }
            // Item allowing a return to original portfolio list as a home
            R.id.item_home -> {
                val intent = Intent(this, PortfolioListActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Image picker is setup for choosing portfolio image
    private fun registerImagePickerCallback() {
        // Image launcher for 1st project image
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            project.projectImage = result.data!!.data!!
                            // Picasso used to get images, as well as standardising sizes and cropping as necessary
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
        // Image launcher for 2nd project image
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
        // Image launcher for 3rd project image
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

    // Map is setup for selecting a location of the project
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
                            // Setting project co-ordinates based on location passed from map
                            project.lat = location.lat
                            project.lng = location.lng
                            project.zoom = location.zoom
                            // Set shown co-ordinates based on location passed from map
                            var formattedLatitude = String.format("%.2f", location.lat); // Limit the decimal places to two
                            binding.projectLatitude.setText("Latitude: $formattedLatitude")
                            var formattedLongitude = String.format("%.2f", location.lng); // Limit the decimal places to two
                            binding.projectLongitude.setText("Longitude: $formattedLongitude")
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}


