package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToExerciseEntryStatsUseCaseImpl @Inject constructor(val navigatorController: AuroraNavigator): NavigateToExerciseEntryStatsUseCase {
    override operator fun invoke(exerciseName: String,date: String) {
            navigatorController.navigate("workout_exercise_stats/${exerciseName}/${date}")
    }

}

class NoOpNavigateToExerciseEntryStatsUseCase(): NavigateToExerciseEntryStatsUseCase{
    override operator fun invoke(date: String,exerciseName: String) {
        TODO("uhh")
    }
}

interface NavigateToExerciseEntryStatsUseCase{
    operator fun invoke(exerciseName: String, date: String) {

    }
}