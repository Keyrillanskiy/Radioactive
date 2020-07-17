package com.github.keyrillanskiy.radioactive.di.modules

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.Module
import dagger.Provides

@Module
class PlayerModule {
    
    @Provides
    fun provideExoPlayer(context: Context): ExoPlayer {
        return SimpleExoPlayer.Builder(context).build()
    }
    
}
