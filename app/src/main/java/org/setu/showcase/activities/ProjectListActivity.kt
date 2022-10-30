package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import org.setu.showcase.R
import org.setu.showcase.adapters.ProjectAdapter
import org.setu.showcase.adapters.ProjectListener
import org.setu.showcase.databinding.ActivityPortfolioBinding
import org.setu.showcase.databinding.ActivityProjectListBinding
import org.setu.showcase.helpers.showImagePicker
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.NewProject
import org.setu.showcase.models.PortfolioModel
import timber.log.Timber

class ProjectListActivity : AppCompatActivity(), ProjectListener {

    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var portfolio = PortfolioModel()
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    val projectBudgets = arrayOf("Show All", "€0-€50K", "€50K-€100K", "€100K-€250K", "€250K-€500K", "€500K-€1M", "€1M+") // Creating array of different project budgets
    private lateinit var binding: ActivityProjectListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invalidateOptionsMenu()
        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarProjectList.title = "Projects" // Helping to signpost in toolbar
        binding.btnNewProject.isVisible = false // This button is now redundant because adding happens in menu, kept in place in case wanting to switch layout in future
        binding.btnBudgetFilter.isVisible = false // This button is now redundant as filtering happens automatically from spinner selection, but kept in case needed in future
        setSupportActionBar(binding.toolbarProjectList)
        app = application as MainApp

        var projectBudget = "Show All" // Set initial project budget to show all projects

        if (intent.hasExtra("portfolio_edit")) { // Collecting portfolio data passed
            portfolio = intent.extras?.getParcelable("portfolio_edit")!! // Setting origin portfolio
            println(portfolio)
            binding.portfolioName.text = portfolio.title // Setting title
            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            // If a specific project budget is selected then the function for loading that type in the layout is selected, otherwise the load all function is selected
            if (projectBudget == "Show All")
            {
                loadProjects()
            } else {
                loadSpecificBudgetProjects(projectBudget)
            }
        }

        // Recognition of data being passed from project activity rather than portfolio activity (e.g. after project edit/creation)
        if (intent.hasExtra("project_addition")) {
            portfolio = intent.extras?.getParcelable("project_addition")!!
            portfolio =  app.portfolios.findPortfolio(portfolio)!! // This allows for re-initiation of portfolio updated JSON file, so any deleted/updated projects are accounted for from the portfolio model
            println(portfolio)
            binding.portfolioName.text = portfolio.title
            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            loadProjects()
        }

        binding.btnNewProject.isVisible = false // This button is now redundant because adding happens in menu, kept in place in case wanting to switch layout in future
        //This button is now redundant because adding happens in menu, kept in place in case wanting to switch layout in future
        /*binding.btnNewProject.setOnClickListener() {
            val launcherIntent = Intent(this, ProjectActivity::class.java)
            launcherIntent.putExtra("portfolio_edit", portfolio)
            refreshIntentLauncher.launch(launcherIntent)
        }*/

        // This button is now redundant as filtering happens automatically from spinner selection, but kept in case needed in future
        /*binding.btnBudgetFilter.setOnClickListener() {
            if (projectBudget == "Show All") {
                loadProjects()
            } else {
                loadSpecificBudgetProjects(projectBudget)
            }
        }*/

        // Setting up the spinner to select the project budget
        val spinner = findViewById<Spinner>(R.id.projectBudgetSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, projectBudgets)
            spinner.adapter = adapter
            // If there is already a project budget selected, the spinner is set to that budget initially
            if (projectBudget != null) {
                val spinnerPosition = adapter.getPosition(projectBudget)
                spinner.setSelection(spinnerPosition)
            }
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    projectBudget = projectBudgets[position] // Index of array and spinner position used to select project budget
                    // The toast message was taken out because it was annoying, but can be reinstated if wanted
                    /*Toast.makeText(this@ProjectListActivity,
                        getString(R.string.selected_item) + " " +
                                "" + projectBudgets[position], Toast.LENGTH_SHORT).show()*/
                    println("this is portfolioType: $projectBudget")
                    if (projectBudget == "Show All") {
                        loadProjects() // If show all selected, show all projects
                    } else {
                        loadSpecificBudgetProjects(projectBudget) // If specific budget selected, show projects of that budget
                    }
                }
                // No problem if nothing selected
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Item allowing return to origin portfolio
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
            // Item allowing addition of a new project, passing necessary portfolio info
            R.id.item_add -> {
                val launcherIntent = Intent(this, ProjectActivity::class.java)
                launcherIntent.putExtra("portfolio_edit", portfolio)
                refreshIntentLauncher.launch(launcherIntent)
            }
            // Item allowing a return to initial portfolio list activity (home)
            R.id.item_home -> {
                val intent = Intent(this, PortfolioListActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Function for clicking on project to edit/view details, with project and portfolio data passed to project activity
    override fun onProjectClick(project: NewProject) {
        val launcherIntent = Intent(this, ProjectActivity::class.java)
        launcherIntent.putExtra("project_edit", project)
        launcherIntent.putExtra("portfolio_edit", portfolio)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadProjects() }
    }

    // Function to load all projects related to a particular portfolio, passing relevant portfolio information
    private fun loadProjects() {
        if (intent.hasExtra("portfolio_edit")) {
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
        }
        var portfolioProjects = portfolio.projects
        println("portfolio: $portfolio")
        println("portfolioProjects: $portfolioProjects")
        if (portfolioProjects != null) {
            showProjects(portfolioProjects.toList())
        }
    }

    // Function to load specific projects related to a particular portfolio and project budget, passing relevant portfolio and project budget information
    private fun loadSpecificBudgetProjects(projectBudget: String) {
        if (intent.hasExtra("portfolio_edit")) {
            portfolio = intent.extras?.getParcelable("portfolio_edit")!! // Set portfolio on passed portfolio
        }
        var portfolioProjects = portfolio.projects // Create variable of projects related to portfolio
        println("portfolio: $portfolio")
        println("portfolioProjects: $portfolioProjects")
        if (portfolioProjects != null) {
            var portfolioProjectList = portfolioProjects.toList() // Create a list from all projects
            var specificPortfolioProjectList = portfolioProjectList.filter { p -> p.projectBudget == projectBudget } // Filter the list for those projects that match budget type
                showProjects(specificPortfolioProjectList) // Show filtered projects
        }
    }

    // Function to show the projects, whether all budgets or specific budgets were selected for loading
    fun showProjects (projects: List<NewProject>) {
        binding.projectRecyclerView.adapter = ProjectAdapter(projects, this)
        binding.projectRecyclerView.adapter?.notifyDataSetChanged()
    }
}