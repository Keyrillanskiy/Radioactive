package com.github.keyrillanskiy.radioactive.presentation.screens.stationslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.keyrillanskiy.radioactive.databinding.FragmentStationsListBinding

/**
 * Fragment for selecting radio stations.
 */
class StationsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStationsListBinding.inflate(inflater, container, false).root
    }

}
