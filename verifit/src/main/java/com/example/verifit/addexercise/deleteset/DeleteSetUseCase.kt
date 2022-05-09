package com.example.verifit.addexercise.deleteset

import com.example.verifit.WorkoutSet
import com.example.verifit.workoutservice.WorkoutService

class DeleteSetUseCase(val workoutService: WorkoutService) {

    operator fun invoke(setIdentifier: String) {
        val workoutSet : WorkoutSet = workoutService.fetchSet(setIdentifier)
        workoutService.removeSet(workoutSet)
    }
}
