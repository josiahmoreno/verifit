package com.example.verifit.common

import androidx.navigation.NavHostController

class GoToAddExerciseUseCase(val navigateTo: ((String)->Unit)) {

    operator fun invoke(exerciseName: String) = navigateTo(exerciseName)


}
