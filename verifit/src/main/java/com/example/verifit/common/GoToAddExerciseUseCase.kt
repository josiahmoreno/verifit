package com.example.verifit.common

import androidx.navigation.NavHostController

class GoToAddExerciseUseCaseImpl(val navigateTo: ((String)->Unit)): GoToAddExerciseUseCase {

    override operator fun invoke(exerciseName: String) = navigateTo(exerciseName)


}

interface GoToAddExerciseUseCase {

    operator fun invoke(exerciseName: String) {

    }


}

class NoOpGoToAddExerciseUseCase(): GoToAddExerciseUseCase {


}
