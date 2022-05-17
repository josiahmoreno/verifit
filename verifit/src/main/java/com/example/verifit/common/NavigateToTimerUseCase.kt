package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

interface NavigateToTimerUseCase {
    operator fun invoke(){

    }
}
class NavigateToTimerUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToTimerUseCase {
    override fun invoke() {
        navHostController.navigate("timer")
    }

}

class NoOpNavigateToTimerUseCase: NavigateToTimerUseCase {

}