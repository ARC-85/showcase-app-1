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
    var edit = false
    val projectBudgets = arrayOf("Show All", "€0-€50K", "€50K-€100K", "€100K-€250K", "€250K-€500K", "€500K-€1M", "€1M+")


    private lateinit var binding: ActivityProjectListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = false
        invalidateOptionsMenu()

        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarProjectList.title = "Projects"
        binding.btnNewProject.isVisible = false
        binding.btnBudgetFilter.isVisible = false


        setSupportActionBar(binding.toolbarProjectList)
        app = application as MainApp

        var projectBudget = "Show All"

        if (intent.hasExtra("portfolio_edit")) {
            edit = true
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
            println(portfolio)
            binding.portfolioName.text = portfolio.title

            binding.btnNewProject.isVisible = true


            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            if (projectBudget == "Show All")
            {
                loadProjects()
            } else {
                loadSpecificBudgetProjects(projectBudget)
            }
            // binding.projectRecyclerView.adapter = ProjectAdapter(app.portfolios.findSpecificProjects(portfolio),this)



        }

        if (intent.hasExtra("project_addition")) {
            edit = true
            portfolio = intent.extras?.getParcelable("project_addition")!!
            portfolio =  app.portfolios.findPortfolio(portfolio)!!
            println(portfolio)
            binding.portfolioName.text = portfolio.title

            binding.btnNewProject.isVisible = true


            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            loadProjects()
            // binding.projectRecyclerView.adapter = ProjectAdapter(app.portfolios.findSpecificProjects(portfolio),this)



        }


        binding.btnNewProject.isVisible = false
        binding.btnNewProject.setOnClickListener() {
            val launcherIntent = Intent(this, ProjectActivity::class.java)

            launcherIntent.putExtra("portfolio_edit", portfolio)
            refreshIntentLauncher.launch(launcherIntent)
        }

        /*binding.btnPortfolioDelete.setOnClickListener() {
            app.portfolios.delete(portfolio)
            val intent = Intent(this, PortfolioListActivity::class.java)
            startActivity(intent)
        }*/
        binding.btnBudgetFilter.setOnClickListener() {
            /*val intent = Intent(this, PortfolioListActivity::class.java)
            println("this is passed portfolioType: $portfolioType")
            intent.putExtra("portfolio_filter", portfolioType)
            startActivity(intent)*/
            if (projectBudget == "Show All") {
                loadProjects()
            } else {
                loadSpecificBudgetProjects(projectBudget)
            }

        }

        val spinner = findViewById<Spinner>(R.id.projectBudgetSpinner)

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
                    Toast.makeText(this@ProjectListActivity,
                        getString(R.string.selected_item) + " " +
                                "" + projectBudgets[position], Toast.LENGTH_SHORT).show()
                    projectBudget = projectBudgets[position]
                    println("this is portfolioType: $projectBudget")
                    if (projectBudget == "Show All") {
                        loadProjects()
                    } else {
                        loadSpecificBudgetProjects(projectBudget)
                    }
                }

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
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                intent.putExtra("portfolio_edit", portfolio)
                startActivity(intent)
            }
            R.id.item_add -> {
                val launcherIntent = Intent(this, ProjectActivity::class.java)

                launcherIntent.putExtra("portfolio_edit", portfolio)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }



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

    private fun loadSpecificBudgetProjects(projectBudget: String) {
        if (intent.hasExtra("portfolio_edit")) {
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
        }

        var portfolioProjects = portfolio.projects
        println("portfolio: $portfolio")
        println("portfolioProjects: $portfolioProjects")
        if (portfolioProjects != null) {
            var portfolioProjectList = portfolioProjects.toList()
            var specificPortfolioProjectList = portfolioProjectList.filter { p -> p.projectBudget == projectBudget }
                showProjects(specificPortfolioProjectList)
        }
    }

    fun showProjects (projects: List<NewProject>) {
        binding.projectRecyclerView.adapter = ProjectAdapter(projects, this)
        binding.projectRecyclerView.adapter?.notifyDataSetChanged()
    }
}