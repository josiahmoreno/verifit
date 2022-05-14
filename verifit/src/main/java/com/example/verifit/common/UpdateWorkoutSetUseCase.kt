package com.example.verifit.common

import android.content.Context
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.Exercise
import com.example.verifit.ExercisesActivity
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutSet
import com.example.verifit.common.NavigateToExercisesListUseCase
import com.example.verifit.exercises.Compose_ExercisesActivity
import com.example.verifit.exercises.ExercisesListDataResult
import com.example.verifit.settings.ToastMaker
import com.example.verifit.workoutservice.WorkoutService

interface UpdateWorkoutSetUseCase {
    operator fun invoke(workoutSet: WorkoutSet) {

    }
}
class NoOpUpdateWorkoutSetUseCase: UpdateWorkoutSetUseCase

class UpdateWorkoutSetUseCaseImpl(val workoutService: WorkoutService): UpdateWorkoutSetUseCase {
    override operator fun invoke(workoutSet: WorkoutSet) {
        workoutService.updateWorkoutSet(workoutSet = workoutSet, dateSelected = workoutSet.date, )
    }
}
