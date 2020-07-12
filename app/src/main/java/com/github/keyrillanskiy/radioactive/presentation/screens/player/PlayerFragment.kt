package com.github.keyrillanskiy.radioactive.presentation.screens.player

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.keyrillanskiy.radioactive.R
import com.github.keyrillanskiy.radioactive.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
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

    lateinit var exoPlayer: ExoPlayer

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

        //test code:
        val context = requireContext()
        val appName = getString(R.string.app_name)
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, appName))
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory, DefaultExtractorsFactory())
            .createMediaSource(Uri.parse("http://sintezfm.radioactivelab.pro:8000/sfm192.aac"))

        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer.prepare(mediaSource)

        playerPlayPauseButton.setOnClickListener {
            if (exoPlayer.playWhenReady) {
                playerPlayPauseButton.setBackgroundResource(R.drawable.ic_play)
                exoPlayer.playWhenReady = false
            } else {
                playerPlayPauseButton.setBackgroundResource(R.drawable.ic_pause)
                exoPlayer.playWhenReady = true
            }
        }
    }

}
