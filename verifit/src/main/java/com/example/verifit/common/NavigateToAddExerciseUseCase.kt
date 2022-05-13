package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigateToAddExerciseUseCaseImpl(val navHostController: NavHostController): NavigateToAddExerciseUseCase {

    override operator fun invoke(exerciseName: String, date: String) {
        val id = navHostController.graph.findStartDestination().id
        val route = navHostController.graph.startDestinationRoute
        Log.d("navhost","12312312321312NavigateToAddExerciseUseCaseImpl $id, route = $route")
        navHostController.navigate("add_exercise/${exerciseName}/$date"){
            //popUpTo(route = root)
        }
    }


}

interface NavigateToAddExerciseUseCase {

    operator fun invoke(exerciseName: String, date: String) {

    }


}

class NoOpNavigateToAddExerciseUseCase(): NavigateToAddExerciseUseCase {


}
