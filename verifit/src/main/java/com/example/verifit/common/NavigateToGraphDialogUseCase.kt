package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToGraphDialogUseCaseImpl(val navigatorController: NavHostController): NavigateToGraphDialogUseCase {
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