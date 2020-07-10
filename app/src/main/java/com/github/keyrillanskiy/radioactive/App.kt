package com.github.keyrillanskiy.radioactive

import android.app.Application
import com.github.keyrillanskiy.radioactive.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom application. Supports Dagger dependency injection for android components.
 */
class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create().inject(this)

        setupLogging()
    }

    private fun setupLogging() {
        Timber.plant(Timber.DebugTree())
        //TODO: plant release tree
    }

}