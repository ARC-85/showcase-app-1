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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.setu.showcase.R
import org.setu.showcase.databinding.ActivityPortfolioBinding
import org.setu.showcase.helpers.showImagePicker
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.PortfolioModel
import timber.log.Timber.i

class PortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPortfolioBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var portfolio = PortfolioModel()
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    val portfolioTypes = arrayOf("New Builds", "Renovations", "Interiors", "Landscaping", "Commercial", "Other")
    var edit = false
    var portfolioType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = false
        invalidateOptionsMenu()
        registerImagePickerCallback()
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarPortfolio.title = "Portfolio"

        binding.portfolioName.isVisible = false
        binding.newPortfolioLabel.isVisible = true
        binding.editPortfolioDetails.isVisible = false
        binding.btnAdd.isVisible = false

        setSupportActionBar(binding.toolbarPortfolio)
        app = application as MainApp
        //loadProjects()

        if (intent.hasExtra("portfolio_edit")) {
            edit = true
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
            println(portfolio)
            binding.portfolioName.text = portfolio.title
            binding.portfolioTitle.setText(portfolio.title)
            binding.description.setText(portfolio.description)
            binding.btnAdd.setText(R.string.save_portfolio)
            binding.btnAdd.isVisible = false

            binding.portfolioName.isVisible = true
            binding.newPortfolioLabel.isVisible = false
            binding.editPortfolioDetails.isVisible = true


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
            println("this is passed portfolio type: $portfolioType")
            portfolio.type = portfolioType
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

        /*binding.btnNewProject.setOnClickListener() {
            val launcherIntent = Intent(this, ProjectActivity::class.java)

            launcherIntent.putExtra("portfolio_edit", portfolio)
            refreshIntentLauncher.launch(launcherIntent)
        }*/

        binding.btnGoToProjects.setOnClickListener() {
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("portfolio_edit", portfolio)
            startActivity(intent)
        }

        val spinner = findViewById<Spinner>(R.id.portfolioTypeSpinner)


        if (edit) {
            portfolioType = portfolio.type
        }

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
                    Toast.makeText(this@PortfolioActivity,
                        getString(R.string.selected_item) + " " +
                                "" + portfolioTypes[position], Toast.LENGTH_SHORT).show()
                    portfolioType = portfolioTypes[position]
                    println("this is portfolioType: $portfolioType")
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_portfolio, menu)
        if (!edit) {
            //menu.getItem(1).isVisible = false
            menu.getItem(2).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioListActivity::class.java)

                startActivity(intent)
            }
            R.id.item_portfolio_delete -> {
                if (intent.hasExtra("portfolio_edit")) {
                    portfolio = intent.extras?.getParcelable("portfolio_edit")!!
                    println("this is the delete portfolio: $portfolio")
                    app.portfolios.delete(portfolio)
                    val intent = Intent(this, PortfolioListActivity::class.java)
                    startActivity(intent)
                }

            }
            R.id.item_portfolio_save -> {
                portfolio.title = binding.portfolioTitle.text.toString()
                portfolio.description = binding.description.text.toString()
                println("this is passed portfolio type: $portfolioType")
                portfolio.type = portfolioType
                if (portfolio.title.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content),R.string.enter_portfolio_title, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (edit) {
                        app.portfolios.update(portfolio.copy())
                        setResult(RESULT_OK)
                        val intent = Intent(this, PortfolioListActivity::class.java)
                        startActivity(intent)
                    } else {
                        app.portfolios.create(portfolio.copy())
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











}