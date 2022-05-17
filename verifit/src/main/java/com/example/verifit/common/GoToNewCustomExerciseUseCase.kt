package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateToNewCustomExerciseCaseImpl @Inject constructor(val navHostController: AuroraNavigator) : GoToNewCustomExerciseCase{
    override operator fun invoke() { navHostController.navigate("new_exercise")}
}

class NoOpNavigateToNewCustomExerciseCase: GoToNewCustomExerciseCase

interface GoToNewCustomExerciseCase {
    operator fun invoke() {

    }
}