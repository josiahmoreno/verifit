package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NavigateToAddExerciseUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToAddExerciseUseCase {

    override operator fun invoke(exerciseName: String, date: String) {
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
