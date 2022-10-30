package org.setu.showcase.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import org.setu.showcase.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler(Looper.getMainLooper())
        val splashTimeOut:Long = 3000 // Hold splash screen for 30 seconds before moving to portfolio list (home)
        handler.postDelayed({
            val intent = Intent(this, PortfolioListActivity::class.java)
            startActivity(intent)
            finish()
        },splashTimeOut)
    }
}