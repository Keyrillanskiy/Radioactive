package com.github.keyrillanskiy.radioactive.di

import com.github.keyrillanskiy.radioactive.App
import com.github.keyrillanskiy.radioactive.di.modules.ApplicationModule
import com.github.keyrillanskiy.radioactive.di.modules.NavigationModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

/**
 * Main Dagger component of the app. Supports code generation for android components.
 */
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, NavigationModule::class])
interface AppComponent : AndroidInjector<App>