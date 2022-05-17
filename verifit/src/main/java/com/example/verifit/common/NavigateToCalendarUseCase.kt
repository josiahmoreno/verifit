package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

interface NavigateToCalendarUseCase {
    operator fun invoke(date: String){

    }
}
class NavigateToCalendarUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToCalendarUseCase {
    override fun invoke(date: String) {
        navHostController.navigate("calendar/$date")
    }

}

class NoOpNavigateToCalendarUseCase: NavigateToCalendarUseCase