package com.example.verifit.navigationhost

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
interface AuroraNavigator {

    fun navigateUp(): Boolean
    fun popBackStack()
    fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }): Boolean
    val destinations: Flow<NavigatorEvent>
}

@Singleton
internal class AuroraNavigatorImpl @Inject constructor() : AuroraNavigator {

    private val navigationEvents = kotlinx.coroutines.channels.Channel<NavigatorEvent>()
    override val destinations = navigationEvents.receiveAsFlow()

    override fun navigateUp(): Boolean = navigationEvents.trySend(NavigatorEvent.NavigateUp).isSuccess
    override fun popBackStack() {
        navigationEvents.trySend(NavigatorEvent.PopBackStack)
    }

    override fun navigate(route: String, builder: NavOptionsBuilder.() -> Unit): Boolean = navigationEvents.trySend(NavigatorEvent.Directions(route, builder)).isSuccess

}

sealed class NavigatorEvent {
    object NavigateUp : NavigatorEvent()
    class Directions(
        val destination: String,
        val builder: NavOptionsBuilder.() -> Unit
    ) : NavigatorEvent()

    object PopBackStack : NavigatorEvent()
}