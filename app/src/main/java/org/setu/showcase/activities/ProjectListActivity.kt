package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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


    private lateinit var binding: ActivityProjectListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = false
        invalidateOptionsMenu()

        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarProjectList.title = "List of Projects"
        binding.btnNewProject.isVisible = false


        setSupportActionBar(binding.toolbarProjectList)
        app = application as MainApp
        //loadProjects()

        if (intent.hasExtra("portfolio_edit")) {
            edit = true
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
            println(portfolio)

            binding.btnNewProject.isVisible = true


            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            loadProjects()
            // binding.projectRecyclerView.adapter = ProjectAdapter(app.portfolios.findSpecificProjects(portfolio),this)



        }



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

    fun showProjects (projects: List<NewProject>) {
        binding.projectRecyclerView.adapter = ProjectAdapter(projects, this)
        binding.projectRecyclerView.adapter?.notifyDataSetChanged()
    }
}