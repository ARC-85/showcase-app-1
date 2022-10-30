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
    val portfolioTypes = arrayOf("New Builds", "Renovations", "Interiors", "Landscaping", "Commercial", "Other") // Creating array of different portfolio types
    var edit = false // Used to show if editing/existing or new portfolio addition
    var portfolioType = "" // Current portfolio type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edit = false
        invalidateOptionsMenu()
        registerImagePickerCallback()
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarPortfolio.title = "Portfolio"
        binding.editPortfolioDetails.isVisible = false //Only shown if editing/existing already
        binding.btnAdd.isVisible = false // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future

        setSupportActionBar(binding.toolbarPortfolio)
        app = application as MainApp


        if (intent.hasExtra("portfolio_edit")) {
            edit = true // Signalling editing/existing portfolio
            portfolio = intent.extras?.getParcelable("portfolio_edit")!!
            println(portfolio)
            binding.portfolioName.text = portfolio.title // Portfolio name displayed
            // Setting the inputted values for title and description
            binding.portfolioTitle.setText(portfolio.title)
            binding.description.setText(portfolio.description)
            binding.btnAdd.setText(R.string.save_portfolio) // Redundant unless changed structure in future
            binding.btnAdd.isVisible = false // Redundant unless changed structure in future
            binding.editPortfolioDetails.isVisible = true // Highlights that details can be edited, after initial creation

            // Used to load portfolio image in standardised format/size using cropping and resizing
            Picasso.get()
                .load(portfolio.image)
                .resize(450, 420)
                .centerCrop()
                .into(binding.portfolioImage)
            if (portfolio.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.button_changeImage)
            }

        }

        // This button is now redundant because saving/adding happens in menu, kept in place in case wanting to switch layout in future
        /* binding.btnAdd.setOnClickListener() {
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
        }*/

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        //Accessing activity listing all projects for the portfolio, passing portfolio information
        binding.btnGoToProjects.setOnClickListener() {
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("portfolio_edit", portfolio)
            startActivity(intent)
        }

        // Setting up the spinner to select the portfolio type
        val spinner = findViewById<Spinner>(R.id.portfolioTypeSpinner)
        // Inputting values if previously selected
        if (edit) {
            portfolioType = portfolio.type
        }
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
                    /*Toast.makeText(this@PortfolioActivity,
                        getString(R.string.selected_item) + " " +
                                "" + portfolioTypes[position], Toast.LENGTH_SHORT).show()*/
                    println("this is portfolioType: $portfolioType")
                }
                // No problem if nothing selected
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_portfolio, menu)
        // If the portfolio is new, the delete item is removed from the menu
        if (!edit) {
            menu.getItem(2).isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Item allowing to go back to initial activity
            R.id.item_cancel -> {
                val intent = Intent(this, PortfolioListActivity::class.java)
                startActivity(intent)
            }
            // Item allowing deletion of portfolio, resulting in a return to initial activity
            R.id.item_portfolio_delete -> {
                if (intent.hasExtra("portfolio_edit")) {
                    portfolio = intent.extras?.getParcelable("portfolio_edit")!!
                    println("this is the delete portfolio: $portfolio")
                    app.portfolios.delete(portfolio)
                    val intent = Intent(this, PortfolioListActivity::class.java)
                    startActivity(intent)
                }
            }
            // Item allowing for saving of portfolio, resulting in a return to initial activity
            R.id.item_portfolio_save -> {
                portfolio.title = binding.portfolioTitle.text.toString()
                portfolio.description = binding.description.text.toString()
                println("this is passed portfolio type: $portfolioType")
                portfolio.type = portfolioType
                // As a minimum, the portfolio title is required, so a snackbar message is displayed if missing
                if (portfolio.title.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content),R.string.enter_portfolio_title, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    if (edit) { // If editing/existing, update is called
                        app.portfolios.update(portfolio.copy())
                        setResult(RESULT_OK)
                        val intent = Intent(this, PortfolioListActivity::class.java)
                        startActivity(intent)
                    } else { // If new portfolio, create is called
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

    // Image picker is setup for choosing portfolio image
    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            portfolio.image = result.data!!.data!!
                            // Picasso used to get images, as well as standardising sizes and cropping as necessary
                            Picasso.get()
                                .load(portfolio.image)
                                .centerCrop()
                                .resize(450, 420)
                                .into(binding.portfolioImage)
                            binding.chooseImage.setText(R.string.button_changeImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}