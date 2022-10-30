package org.setu.showcase.main

import android.app.Application
import org.setu.showcase.models.PortfolioJSONStore
import org.setu.showcase.models.PortfolioMemStore
import org.setu.showcase.models.PortfolioStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var portfolios: PortfolioStore
    //lateinit var projects: PortfolioStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        //portfolios = PortfolioMemStore()
        portfolios = PortfolioJSONStore(applicationContext)
        //projects = PortfolioMemStore()
        i("Showcase started")
    }
}