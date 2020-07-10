package com.github.keyrillanskiy.radioactive.di

import com.github.keyrillanskiy.radioactive.presentation.screens.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module for injecting android components.
 */
@Module
interface ApplicationModule {
    @ContributesAndroidInjector
    fun contributeActivityInjector(): MainActivity
}