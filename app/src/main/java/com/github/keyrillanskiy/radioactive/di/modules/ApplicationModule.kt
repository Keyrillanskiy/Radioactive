package com.github.keyrillanskiy.radioactive.di.modules

import com.github.keyrillanskiy.radioactive.data.player.PlayerMediaBrowserService
import com.github.keyrillanskiy.radioactive.presentation.screens.main.MainActivity
import com.github.keyrillanskiy.radioactive.presentation.screens.player.PlayerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module for injecting android components.
 */
@Module
interface ApplicationModule {
    @ContributesAndroidInjector
    fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    fun contributePlayerFragmentInjector(): PlayerFragment
    
    @ContributesAndroidInjector
    fun contributePlayerServiceInjector(): PlayerMediaBrowserService
}
