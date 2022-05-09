package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToDeleteSetDialogUseCaseImpl(val navigatorController: NavHostController): NavigateToDeleteSetDialogUseCase {
    override operator fun invoke(identifier: String) {
            navigatorController.navigate("delete_set/${identifier}")
    }

}

class NoOpNavigateToDeleteSetDialogUseCase(): NavigateToDeleteSetDialogUseCase

interface NavigateToDeleteSetDialogUseCase{
    operator fun invoke(identifier: String) {

    }
}