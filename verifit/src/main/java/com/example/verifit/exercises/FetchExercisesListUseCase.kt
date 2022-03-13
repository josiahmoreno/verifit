package com.example.verifit.exercises

import com.example.verifit.Exercise
import com.example.verifit.KnownExerciseService
import com.example.verifit.main.FetchViewPagerDataResult

class FetchExercisesListUseCase(val knownExerciseService: KnownExerciseService) {
    operator fun invoke(): ExercisesListDataResult = fetchExercisesList()

    private fun fetchExercisesList():ExercisesListDataResult{
        return ExercisesListDataResult(knownExerciseService.knownExercises)
    }

}