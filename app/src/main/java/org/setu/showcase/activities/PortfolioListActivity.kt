package org.setu.showcase.activities

import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import org.setu.showcase.R
import org.setu.showcase.adapters.PortfolioAdapter
import org.setu.showcase.adapters.PortfolioListener
import org.setu.showcase.databinding.ActivityPortfolioListBinding
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.NewProject
import org.setu.showcase.models.PortfolioModel

class PortfolioListActivity : AppCompatActivity(), PortfolioListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityPortfolioListBinding
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    val portfolioTypes = arrayOf("Show All", "New Builds", "Renovations", "Interiors", "Landscaping", "Commercial", "Other") // Creating array of different portfolio types
    var mapProjects = mutableListOf<NewProject>() // List of projects to send to map, this is just used as a test (not functional)
    var portfolioType = "" // Selected portfolio type for filtering list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        app = application as MainApp
        binding.btnFilter.isVisible = false // This button is now redundant as filtering happens automatically from spinner selection, but kept in case needed in future
        portfolioType = "Show All" // Initially show all portfolios, unless otherwise selected

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        // If a specific portfolio type is selected then the function for loading that type in the layout is selected, otherwise the load all function is selected
        if (portfolioType == "Show All") {
            loadPortfolios()
        } else {
            loadSpecificPortfolios(portfolioType)
        }

        registerMapCallback()

        // Setting up the spinner to select the portfolio type
        val spinner = findViewById<Spinner>(R.id.portfolioTypeSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, portfolioTypes)
            spinner.adapter = adapter
            // If there is already a portfolio type selected, the spinner is set to that type initially
            if (portfolioType != null) {
                val spinnerPosition = adapter.getPosition(portfolioType)
                spinner.setSelection(spinnerPosition)
            }
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    portfolioType = portfolioTypes[position] // Index of array and spinner position used to select portfolio type
                    // The toast message was taken out because it was annoying, but can be reinstated if wanted
                    /*Toast.makeText(this@PortfolioListActivity,
                        getString(R.string.selected_item) + " " +
                                "" + portfolioTypes[position], Toast.LENGTH_SHORT).show()*/
                    println("this is portfolioType: $portfolioType")
                    // A test, mapProjects, was created to check the projects being passed to the overall project map (non-functional)
                    if (portfolioType == "Show All") {
                        loadPortfolios() // If show all selected, show all portfolios
                        mapProjects = app.portfolios.findProjects().toMutableList()
                        println("this is show all mapProjects: $mapProjects")
                    } else {
                        loadSpecificPortfolios(portfolioType) // If specific type selected, show portfolios of that type
                        mapProjects = app.portfolios.findSpecificTypeProjects(portfolioType)
                        println("this is specific mapProjects: $mapProjects")
                    }
                }
                // No problem if nothing selected
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        // This button is now redundant as filtering happens automatically from spinner selection, but kept in case needed in future
        binding.btnFilter.setOnClickListener() {
            if (portfolioType == "Show All") {
                loadPortfolios()
            } else {
                loadSpecificPortfolios(portfolioType)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Item allowing a new portfolio to be added
            R.id.item_add -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                startActivity(intent)
            }
            // Item allowing a view of the map of all projects. Portfolio type is passed to allow for filtering of projects shown based on selected portfolio type.
            R.id.item_map -> {
                val launcherIntent = Intent(this, ProjectMapsActivity::class.java)
                    .putExtra("portfolio_type", portfolioType)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Accessing an existing portfolio
    override fun onPortfolioClick(portfolio: PortfolioModel) {
        val intent = Intent(this, PortfolioActivity::class.java)
        println("this is passed portfolio: $portfolio")
        intent.putExtra("portfolio_edit", portfolio)
        startActivity(intent)
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }

    // Function for loading all portfolios
    private fun loadPortfolios() {
        println("load general portfolios")
        showPortfolios(app.portfolios.findAll())
    }

    // Function for loading portfolios related to a specific type, where portfolio type is passed
    private fun loadSpecificPortfolios(portfolioType: String) {
        println("load specific portfolios")
        showPortfolios(app.portfolios.findSpecificPortfolios(portfolioType))
    }

    // Function to show the portfolios, whether all types or specific types was selected for loading
    fun showPortfolios (portfolios: List<PortfolioModel>) {
        binding.recyclerView.adapter = PortfolioAdapter(portfolios, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}
