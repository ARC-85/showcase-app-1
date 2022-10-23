package org.setu.showcase.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.setu.showcase.R
import org.setu.showcase.adapters.PortfolioAdapter
import org.setu.showcase.databinding.ActivityPortfolioBinding
import org.setu.showcase.helpers.showImagePicker
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.PortfolioModel
import org.setu.showcase.models.NewProject
import org.setu.showcase.adapters.ProjectAdapter
import org.setu.showcase.adapters.ProjectListener
import timber.log.Timber.i

class PortfolioActivity : AppCompatActivity(), ProjectListener {
    private lateinit var binding: ActivityPortfolioBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var portfolio = PortfolioModel()
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = false
        invalidateOptionsMenu()
        registerImagePickerCallback()
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp
        //loadProjects()

        if (intent.hasExtra("portfolio_edit")) {
            edit = true
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
            println(portfolio)
            binding.portfolioTitle.setText(portfolio.title)
            binding.description.setText(portfolio.description)
            binding.btnAdd.setText(R.string.save_portfolio)
            val layoutManager = LinearLayoutManager(this)
            binding.projectRecyclerView.layoutManager = layoutManager
            loadProjects()
            // binding.projectRecyclerView.adapter = ProjectAdapter(app.portfolios.findSpecificProjects(portfolio),this)

            Picasso.get()
                .load(portfolio.image)
                .into(binding.portfolioImage)
            if (portfolio.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.button_changeImage)
            }

        }

        binding.btnAdd.setOnClickListener() {

            portfolio.title = binding.portfolioTitle.text.toString()
            portfolio.description = binding.description.text.toString()
            if (portfolio.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_portfolio_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.portfolios.update(portfolio.copy())
                } else {
                    app.portfolios.create(portfolio.copy())
                }
            }
            setResult(RESULT_OK)
            val intent = Intent(this, PortfolioListActivity::class.java)

            startActivity(intent)
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.btnPortfolioDelete.setOnClickListener() {
            app.portfolios.delete(portfolio)
            val intent = Intent(this, PortfolioListActivity::class.java)
            startActivity(intent)
        }



        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_portfolio, menu)
        if (!edit) {
            menu.getItem(1).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioListActivity::class.java)

                startActivity(intent)
            }
            R.id.project_add -> {
                val launcherIntent = Intent(this, ProjectActivity::class.java)

                launcherIntent.putExtra("portfolio_edit", portfolio)
                refreshIntentLauncher.launch(launcherIntent)
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
                            portfolio.image = result.data!!.data!!
                            Picasso.get()
                                .load(portfolio.image)
                                .into(binding.portfolioImage)
                            binding.chooseImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
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