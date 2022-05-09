package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToExerciseEntryStatsUseCaseImpl(val navigatorController: NavHostController): NavigateToExerciseEntryStatsUseCase {
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