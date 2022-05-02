package com.example.verifit.addexercise.history

import com.example.verifit.WorkoutExercise
import com.example.verifit.workoutservice.WorkoutService

class FetchHistoryUseCase(val workoutService: WorkoutService) {

    operator fun invoke(exerciseName: String): List<WorkoutExercise> {
        return workoutService.getExercisesWithName(exerciseName).reversed()
    }
}
