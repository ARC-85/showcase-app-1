package org.setu.showcase.main

import android.app.Application
import org.setu.showcase.models.PortfolioMemStore
import org.setu.showcase.models.PortfolioModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    // val portfolios = ArrayList<PortfolioModel>()
    val portfolios = PortfolioMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Showcase started")
        // portfolios.add(PortfolioModel("One", "About one..."))
        // portfolios.add(PortfolioModel("Two", "About two..."))
        // portfolios.add(PortfolioModel("Three", "About three..."))
    }
}