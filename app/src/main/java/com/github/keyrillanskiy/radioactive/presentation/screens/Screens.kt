package com.github.keyrillanskiy.radioactive.presentation.screens

import androidx.fragment.app.Fragment
import com.github.keyrillanskiy.radioactive.presentation.screens.player.PlayerFragment
import com.github.keyrillanskiy.radioactive.presentation.screens.stationslist.StationsListFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

sealed class Screens : SupportAppScreen() {

    class PlayerScreen : Screens() {
        override fun getFragment(): Fragment {
            return PlayerFragment()
        }
    }

    class StationsListScreen : Screens() {
        override fun getFragment(): Fragment {
            return StationsListFragment()
        }
    }

}