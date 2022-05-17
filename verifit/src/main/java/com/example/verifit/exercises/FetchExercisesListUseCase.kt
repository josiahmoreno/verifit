package com.example.verifit.exercises

import com.example.verifit.Exercise
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutCategory
import com.example.verifit.main.FetchViewPagerDataResult

class FetchExercisesListUseCase(val knownExerciseService: KnownExerciseService) {
    operator fun invoke(): ExercisesListDataResult = fetchExercisesList()

    private fun fetchExercisesList():ExercisesListDataResult{
        return ExercisesListDataResult(//knownExerciseService.knownExercises,
            knownExerciseService.fetchCategories().map { ExerciseListResult.Category(it,
             knownExerciseService.fetchExecisesOfCategory(it.category).map { exercise ->
                 ExerciseListResult.ExerciseItem(exercise)
             }
                ) } )
    }

     fun fetch2():ExerciseListResult2.Category{
        return ExerciseListResult2.Category(//knownExerciseService.knownExercises,
            knownExerciseService.fetchCategories().map { WorkoutCategoryItem(it,
                knownExerciseService.fetchExecisesOfCategory(it.category).map { exercise ->
                   (exercise)
                }
            ) } )
    }

    fun fetch3():ExerciseListResult2.Exercises{
        return ExerciseListResult2.Exercises(//knownExerciseService.knownExercises,
            knownExerciseService.knownExercises )
    }

}