package org.setu.showcase.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.setu.showcase.R
import org.setu.showcase.databinding.ActivityShowcaseBinding
import org.setu.showcase.helpers.showImagePicker
import org.setu.showcase.main.MainApp
import org.setu.showcase.models.ShowcaseModel
import timber.log.Timber
import timber.log.Timber.i

class ShowcaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowcaseBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    var showcase = ShowcaseModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false
        registerImagePickerCallback()
        binding = ActivityShowcaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

        if (intent.hasExtra("showcase_edit")) {
            edit = true
            showcase = intent.extras?.getParcelable("showcase_edit")!!
            binding.showcaseTitle.setText(showcase.title)
            binding.description.setText(showcase.description)
            binding.btnAdd.setText(R.string.save_showcase)
            binding.chooseImage.setText(R.string.button_changeImage)
            Picasso.get()
                .load(showcase.image)
                .into(binding.showcaseImage)
        }

        binding.btnAdd.setOnClickListener() {
            showcase.title = binding.showcaseTitle.text.toString()
            showcase.description = binding.description.text.toString()
            if (showcase.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_showcase_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.showcases.update(showcase.copy())
                } else {
                    app.showcases.create(showcase.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_showcase, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
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
                            showcase.image = result.data!!.data!!
                            Picasso.get()
                                .load(showcase.image)
                                .into(binding.showcaseImage)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

}