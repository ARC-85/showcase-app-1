package org.setu.showcase.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        loadPortfolios()

        registerRefreshCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, PortfolioActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, ProjectMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPortfolioClick(portfolio: PortfolioModel) {
        val launcherIntent = Intent(this, PortfolioActivity::class.java)
        launcherIntent.putExtra("portfolio_edit", portfolio)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadPortfolios() }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {  }
    }

    private fun loadPortfolios() {
        showPortfolios(app.portfolios.findAll())
    }

    fun showPortfolios (portfolios: List<PortfolioModel>) {
        binding.recyclerView.adapter = PortfolioAdapter(portfolios, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}
