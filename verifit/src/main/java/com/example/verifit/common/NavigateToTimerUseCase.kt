package com.example.verifit.common

import androidx.navigation.NavHostController

interface NavigateToTimerUseCase {
    operator fun invoke(){

    }
}
class NavigateToTimerUseCaseImpl(val navHostController: NavHostController): NavigateToTimerUseCase {
    override fun invoke() {
        navHostController.navigate("timer")
    }

}

class NoOpNavigateToTimerUseCase: NavigateToTimerUseCase {

}