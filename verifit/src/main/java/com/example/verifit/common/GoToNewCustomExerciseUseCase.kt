package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToNewCustomExerciseCaseImpl (val navHostController: NavHostController) : GoToNewCustomExerciseCase{
    override operator fun invoke() = navHostController.navigate("new_exercise")
}

class NoOpNavigateToNewCustomExerciseCase: GoToNewCustomExerciseCase

interface GoToNewCustomExerciseCase {
    operator fun invoke() {

    }
}