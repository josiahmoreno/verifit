package com.example.verifit.common

import androidx.navigation.NavHostController

interface NavigateToCalendarUseCase {
    operator fun invoke(date: String){

    }
}
class NavigateToCalendarUseCaseImpl(val navHostController: NavHostController): NavigateToCalendarUseCase {
    override fun invoke(date: String) {
        navHostController.navigate("calendar/$date")
    }

}

class NoOpNavigateToCalendarUseCase: NavigateToCalendarUseCase