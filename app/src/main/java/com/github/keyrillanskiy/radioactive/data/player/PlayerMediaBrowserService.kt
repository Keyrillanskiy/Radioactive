package com.github.keyrillanskiy.radioactive.data.player

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.github.keyrillanskiy.radioactive.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerMediaBrowserService : MediaBrowserServiceCompat() {

    private val TAG = this::class.java.simpleName
    private val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    private val TEMP_RADIO_URL = "http://sintezfm.radioactivelab.pro:8000/sfm192.aac"

    private val exoPlayer: ExoPlayer = SimpleExoPlayer.Builder(applicationContext).build()
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var mediaSourceFactory: ProgressiveMediaSource.Factory
    private val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest

    private val sessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            play()
        }

        override fun onPause() {
            pause()
        }

        override fun onStop() {
            stop()
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focus ->
        when (focus) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                //todo
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                //todo
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                //todo
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                //todo
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)

        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(stateBuilder.build())
            setCallback(sessionCallback)
            setSessionToken(sessionToken)
        }

        requestAudioFocus()
        initPlayer()
    }

    override fun onDestroy() {
        //todo clean up resources
        removeAudioFocus()
        super.onDestroy()
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    private fun initPlayer() {
        val appName = getString(R.string.app_name)
        val context = applicationContext
        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, appName))
        mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory, DefaultExtractorsFactory())
        exoPlayer.prepare(mediaSourceFactory.createMediaSource(Uri.parse(TEMP_RADIO_URL)))
    }

    private fun play() {
        exoPlayer.playWhenReady = true
    }

    private fun pause() {
        exoPlayer.playWhenReady = false
    }

    private fun stop() {
        //todo clean up resources
        stopSelf()
    }

    private fun changeStation(url: String) {
        exoPlayer.prepare(mediaSourceFactory.createMediaSource(Uri.parse(url)))
    }

    private fun requestAudioFocus(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioManager.requestAudioFocus(audioFocusRequest)
        } else {
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun removeAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

}
