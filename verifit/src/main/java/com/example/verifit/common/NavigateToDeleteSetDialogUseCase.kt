package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateToDeleteSetDialogUseCaseImpl @Inject constructor(val navigatorController: AuroraNavigator): NavigateToDeleteSetDialogUseCase {
    init {
        Log.d("NavigateToDeleteSetDialogUseCaseImpl", "init")
    }
    override operator fun invoke(identifier: String) {
            navigatorController.navigate("delete_set/${identifier}")
    }

}

class NoOpNavigateToDeleteSetDialogUseCase(): NavigateToDeleteSetDialogUseCase

interface NavigateToDeleteSetDialogUseCase{
    operator fun invoke(identifier: String) {

    }
}