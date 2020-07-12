package com.github.keyrillanskiy.radioactive.presentation.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.keyrillanskiy.radioactive.R
import com.github.keyrillanskiy.radioactive.presentation.screens.Screens
import dagger.android.AndroidInjection
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import javax.inject.Inject

/**
 * "Single-activity" container. 
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigator = SupportAppNavigator(this, R.id.mainFragmentContainer)

        router.navigateTo(Screens.PlayerScreen())
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

}
