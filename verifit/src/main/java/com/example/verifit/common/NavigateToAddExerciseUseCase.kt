package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToAddExerciseUseCaseImpl(val navHostController: NavHostController): NavigateToAddExerciseUseCase {

    override operator fun invoke(exerciseName: String, date: String) {
        navHostController.navigate("add_exercise/${exerciseName}/$date")
    }


}

interface NavigateToAddExerciseUseCase {

    operator fun invoke(exerciseName: String, date: String) {

    }


}

class NoOpNavigateToAddExerciseUseCase(): NavigateToAddExerciseUseCase {


}
