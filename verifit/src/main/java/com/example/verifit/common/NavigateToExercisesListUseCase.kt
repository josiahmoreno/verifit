package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToExercisesListUseCaseImpl(val navHostController: NavHostController): NavigateToExercisesListUseCase {
    override operator fun invoke() = navHostController.navigate("list")
}

class MockNavigateToExercisesListUseCase(): NavigateToExercisesListUseCase {
    override fun invoke() {

    }

}

interface NavigateToExercisesListUseCase {
     operator fun invoke()
}