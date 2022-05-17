package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

interface NavigateToSettingsUseCase {
    operator fun invoke(){

    }
}
class NavigateToSettingsUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToSettingsUseCase {
    override fun invoke() {
        navHostController.navigate("settings")
    }

}

class NoOpNavigateToSettingsUseCase: NavigateToSettingsUseCase {

}