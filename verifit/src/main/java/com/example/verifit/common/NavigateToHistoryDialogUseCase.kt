package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToHistoryDialogUseCaseImpl(val navigatorController: NavHostController): NavigateToHistoryDialogUseCase {
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