package com.github.keyrillanskiy.radioactive.di

import android.content.Context
import com.github.keyrillanskiy.radioactive.App
import com.github.keyrillanskiy.radioactive.di.modules.ApplicationModule
import com.github.keyrillanskiy.radioactive.di.modules.NavigationModule
import com.github.keyrillanskiy.radioactive.di.modules.PlayerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

/**
 * Main Dagger component of the app. Supports code generation for android components.
 */
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, NavigationModule::class, PlayerModule::class])
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
    
}
