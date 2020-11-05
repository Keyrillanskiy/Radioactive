package com.github.keyrillanskiy.radioactive.data.player

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.github.keyrillanskiy.radioactive.R
import com.github.keyrillanskiy.radioactive.presentation.screens.main.MainActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.android.AndroidInjection
import javax.inject.Inject


class PlayerMediaBrowserService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer
    
    private var mediaSession: MediaSessionCompat? = null
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var mediaSourceFactory: ProgressiveMediaSource.Factory
    private lateinit var audioManager: AudioManager 
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
                changeVolume(FULL_VOLUME)
                play()
            }
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> changeVolume(HALF_OF_A_VOLUME)
        }
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                pause()
            }
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)

        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(playbackStateBuilder.build())
            setCallback(sessionCallback)
            //setSessionToken(sessionToken) todo
            isActive = true
        }
        sessionToken = mediaSession?.sessionToken

        //activity to be launched when player notification clicked
        val activityIntent = Intent(baseContext, MainActivity::class.java)
        mediaSession?.setSessionActivity(PendingIntent.getActivity(baseContext, 0, activityIntent, 0))

        initPlayer()
    }

    override fun onDestroy() {
        exoPlayer.release()
        mediaSession?.release()
        stop()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
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
        if (requestAudioFocus().not()) {
            return
        }

        val metadata =
            metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Radio title") //todo change title
                .build()
        mediaSession?.let { session ->
            session.setMetadata(metadata)
            session.isActive = true
            session.updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }
        
        registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))

        exoPlayer.playWhenReady = true
    }

    private fun pause() {
        exoPlayer.apply {
            playWhenReady = false
            if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                mediaSession?.updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
        unregisterReceiver(becomingNoisyReceiver)
    }

    private fun stop() {
        exoPlayer.playWhenReady = false
        
        mediaSession?.isActive = false
        mediaSession?.updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)

        removeAudioFocus()
        unregisterReceiver(becomingNoisyReceiver)
        
        stopSelf()
    }

    private fun changeVolume(volume: Float) {
        exoPlayer.volume = volume
    }

    private fun MediaSessionCompat.updatePlaybackState(state: Int) {
        val playbackState = playbackStateBuilder.setState(state, PLAYBACK_POSITION, PLAYBACK_SPEED).build()
        this.setPlaybackState(playbackState)
        refreshForegroundAndNotificationStatus(state)
    }

    private fun changeStation(url: String) {
        exoPlayer.prepare(mediaSourceFactory.createMediaSource(Uri.parse(url))) //TODO: optimize with map (I guess)
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

    private fun refreshForegroundAndNotificationStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> startForeground(NOTIFICATION_ID, getNotification(playbackState))
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, getNotification(playbackState))
                stopForeground(false)
            }
            else -> stopForeground(true)
        }
    }

    private fun getNotification(playbackState: Int): Notification {
        val title = mediaSession?.controller?.metadata?.description?.title ?: getString(R.string.unknown)

        val notificationBuilder = NotificationCompat.Builder(baseContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(NOTIFICATION_ACTION_COUNT)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)
                    )
                    .setMediaSession(mediaSession?.sessionToken) // setMediaSession required for Android Wear
            )
            .setShowWhen(false)
            .setOnlyAlertOnce(true)

        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)
                )
            )
            PlaybackStateCompat.STATE_PAUSED -> notificationBuilder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    getString(R.string.play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)
                )
            )
        }

        return notificationBuilder.build()
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
        private const val TEMP_RADIO_URL = "http://sintezfm.radioactivelab.pro:8000/sfm192.aac"
        private const val PLAYBACK_POSITION = 0L
        private const val PLAYBACK_SPEED = 1.0f
        private const val FULL_VOLUME = 1.0f
        private const val HALF_OF_A_VOLUME = 0.5f
        private const val NOTIFICATION_ACTION_COUNT = 2
        const val NOTIFICATION_ID = 1488
        const val NOTIFICATION_CHANNEL_ID = "radioactive_notification_channel_id"
    }

}
