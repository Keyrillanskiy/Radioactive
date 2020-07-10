package com.github.keyrillanskiy.radioactive.di

import com.github.keyrillanskiy.radioactive.App
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

/**
 * Main Dagger component of the app. Supports code generation for android components.
 */
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class])
interface AppComponent : AndroidInjector<App>