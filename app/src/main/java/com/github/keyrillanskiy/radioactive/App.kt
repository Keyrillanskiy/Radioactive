package com.github.keyrillanskiy.radioactive

import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupLogging()
    }

    private fun setupLogging() {
        Timber.plant(Timber.DebugTree())
        //TODO: plant release tree
    }

}