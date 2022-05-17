package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject


class NavigateToHistoryDialogUseCaseImpl @Inject constructor(val navigatorController: AuroraNavigator): NavigateToHistoryDialogUseCase {
    override operator fun invoke(exerciseName: String) {
            navigatorController.navigate("history_dialog/${exerciseName}")
    }

}

class NoOpNavigateToHistoryDialogUseCase(): NavigateToHistoryDialogUseCase{
    override fun invoke(exerciseName: String) {
        //TODO("Not yet implemented")
    }

}

interface NavigateToHistoryDialogUseCase{
    operator fun invoke(exerciseName: String)
}