package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToGraphDialogUseCaseImpl @Inject constructor(val navigatorController: AuroraNavigator): NavigateToGraphDialogUseCase {
    override operator fun invoke(exerciseName: String) {
            navigatorController.navigate("graph/${exerciseName}")
    }

}

class NoOpNavigateToGraphDialogUseCase(): NavigateToGraphDialogUseCase{
    override fun invoke(exerciseName: String) {
        //TODO("Not yet implemented")
    }

}

interface NavigateToGraphDialogUseCase{
    operator fun invoke(exerciseName: String)
}