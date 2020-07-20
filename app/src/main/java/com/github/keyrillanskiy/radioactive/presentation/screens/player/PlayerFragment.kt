package com.github.keyrillanskiy.radioactive.presentation.screens.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.keyrillanskiy.radioactive.R
import com.github.keyrillanskiy.radioactive.data.player.PlayerMediaBrowserService
import com.github.keyrillanskiy.radioactive.databinding.FragmentPlayerBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_player.*
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 * Fragment for listening a radio.
 */
class PlayerFragment : Fragment() {

    @Inject
    lateinit var router: Router

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            val mediaController = MediaControllerCompat(requireContext(), mediaBrowser.sessionToken)
            MediaControllerCompat.setMediaController(requireActivity(), mediaController)
            buildTransportControls(mediaController)
        }
    }

    private val controllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            playerSongTitleTextView.text = metadata?.description?.title ?: ""
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if(state?.state == PlaybackStateCompat.STATE_PLAYING) {
                playerPlayPauseButton.setBackgroundResource(R.drawable.ic_pause)
            } else {
                playerPlayPauseButton.setBackgroundResource(R.drawable.ic_play)
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPlayerBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()

        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, PlayerMediaBrowserService::class.java),
            connectionCallbacks,
            null
        )
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        MediaControllerCompat.getMediaController(requireActivity()).unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
        super.onStop()
    }

    fun buildTransportControls(mediaController: MediaControllerCompat) {
        playerPlayPauseButton.apply {
            setOnClickListener {
                val playbackState = mediaController.playbackState.state
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                } else {
                    mediaController.transportControls.play()
                }
            }
        }

        //TODO: Display the initial state
        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(controllerCallback)
    }

}
