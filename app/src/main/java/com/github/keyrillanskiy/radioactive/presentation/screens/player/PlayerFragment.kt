package com.github.keyrillanskiy.radioactive.presentation.screens.player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.keyrillanskiy.radioactive.databinding.FragmentPlayerBinding
import dagger.android.support.AndroidSupportInjection
import ru.terrakok.cicerone.Router
import javax.inject.Inject

/**
 * Fragment for listening a radio.
 */
class PlayerFragment : Fragment() {

    @Inject
    lateinit var router: Router

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

}
