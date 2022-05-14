package com.example.verifit.singleday

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.example.verifit.ColorGetter
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.main.ExerciseLiveData
import com.example.verifit.main.WorkoutExercisesViewData
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService


interface FetchDaysWorkoutsUseCase{
    operator fun invoke(date: String) : Results


    interface Results {
        val data: WorkoutExercisesViewData
    }
    data class ResultsImpl(
            override val data : WorkoutExercisesViewData,
            val day: WorkoutDay
    ): Results
    data class MockResults(
            override val data : WorkoutExercisesViewData
    ): Results

}
class FetchDaysWorkoutsUseCaseImpl(val workoutService: WorkoutService,
                                   private val colorGetter: ColorGetter

                                   ) : FetchDaysWorkoutsUseCase{

    override operator fun invoke(date: String): FetchDaysWorkoutsUseCase.Results = fetch(date)

    private fun fetch(date: String): FetchDaysWorkoutsUseCase.Results {
        val day = workoutService.fetchWorkoutDays().find {
            it.date == date
        }!!
        return FetchDaysWorkoutsUseCase.ResultsImpl(WorkoutExercisesViewData(
                MutableLiveData(
                        day.exercises.map { workoutExercise ->
                            Pair( first = ExerciseLiveData( exerciseLiveData = workoutService.fetchWorkoutExercise(workoutExercise.exercise,workoutExercise.date)),
                                second =
                            Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
                        }
                )
            ), day
        )
    }
}

class MockFetchDaysWorkoutsUseCase(val data: WorkoutExercisesViewData): FetchDaysWorkoutsUseCase {
    override fun invoke(date: String) : FetchDaysWorkoutsUseCase.Results = FetchDaysWorkoutsUseCase.MockResults(data)
}

