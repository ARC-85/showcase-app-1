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
import org.setu.showcase.models.PortfolioModel

class PortfolioListActivity : AppCompatActivity(), PortfolioListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityPortfolioListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    val portfolioTypes = arrayOf("Show All", "New Builds", "Renovations", "Interiors", "Landscaping", "Commercial", "Other")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        app = application as MainApp
        binding.btnFilter.isVisible = false

        var portfolioType = "Show All"



        /*if (intent.hasExtra("portfolio_filter")) {
            portfolioType = intent.extras?.getParcelable("portfolio_filter")!!
        }*/

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        if (portfolioType == "Show All") {
            loadPortfolios()
        } else {
            loadSpecificPortfolios(portfolioType)
        }


        //registerRefreshCallback()
        registerMapCallback()

        val spinner = findViewById<Spinner>(R.id.portfolioTypeSpinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, portfolioTypes)
            spinner.adapter = adapter
            if (portfolioType != null) {
                val spinnerPosition = adapter.getPosition(portfolioType)
                spinner.setSelection(spinnerPosition)
            }

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    portfolioType = portfolioTypes[position]
                    Toast.makeText(this@PortfolioListActivity,
                        getString(R.string.selected_item) + " " +
                                "" + portfolioTypes[position], Toast.LENGTH_SHORT).show()
                    portfolioType = portfolioTypes[position]
                    println("this is portfolioType: $portfolioType")
                    if (portfolioType == "Show All") {
                        loadPortfolios()
                    } else {
                        loadSpecificPortfolios(portfolioType)
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }

            }
        }

        binding.btnFilter.setOnClickListener() {
            /*val intent = Intent(this, PortfolioListActivity::class.java)
            println("this is passed portfolioType: $portfolioType")
            intent.putExtra("portfolio_filter", portfolioType)
            startActivity(intent)*/
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
            R.id.item_add -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                startActivity(intent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, ProjectMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPortfolioClick(portfolio: PortfolioModel) {
        /*val launcherIntent = Intent(this, PortfolioActivity::class.java)
        launcherIntent.putExtra("portfolio_edit", portfolio)
        refreshIntentLauncher.launch(launcherIntent)*/
        val intent = Intent(this, PortfolioActivity::class.java)
        println("this is passed portfolio: $portfolio")
        intent.putExtra("portfolio_edit", portfolio)
        startActivity(intent)
    }

    /*private fun registerRefreshCallback() {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        //{ loadSpecificPortfolios(portfolioType) }
        { loadPortfolios() }
        /*refreshIntentLauncher = if (portfolioType == "Show ALl") {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            //{ loadSpecificPortfolios(portfolioType) }
            { loadPortfolios() }
        } else {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadSpecificPortfolios(portfolioType) }
        }*/

    }*/

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }

    private fun loadPortfolios() {
        println("load general portfolios")
        showPortfolios(app.portfolios.findAll())
    }

    private fun loadSpecificPortfolios(portfolioType: String) {
        println("load specific portfolios")
        showPortfolios(app.portfolios.findSpecificPortfolios(portfolioType))
    }

    fun showPortfolios (portfolios: List<PortfolioModel>) {
        binding.recyclerView.adapter = PortfolioAdapter(portfolios, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }


}
