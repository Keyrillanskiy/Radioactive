package com.github.keyrillanskiy.radioactive.di.modules

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.Module
import dagger.Provides

@Module
class PlayerModule {
    
    @Provides
    fun provideSimpleExoPlayer(context: Context): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context).build() //TODO: refactor
    }
    
}
