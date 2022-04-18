package com.example.verifit.singleday

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.example.verifit.GetCategoryIconTint
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.diary.DiaryEntry
import com.example.verifit.diary.FetchDiaryUseCase
import com.example.verifit.main.FetchViewPagerDataResult
import com.example.verifit.main.WorkoutExercisesViewData
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService


interface FetchDaysWorkoutsUseCase{
    operator fun invoke() : Results


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
                                   val dateSelectStore: DateSelectStore,
                                   private val knownExerciseService: KnownExerciseService) : FetchDaysWorkoutsUseCase{

    override operator fun invoke(): FetchDaysWorkoutsUseCase.Results = fetch()

    private fun fetch(): FetchDaysWorkoutsUseCase.Results {
        val day = workoutService.fetchWorkoutDays().find {
            it.date == dateSelectStore.date_selected
        }!!
        return FetchDaysWorkoutsUseCase.ResultsImpl(WorkoutExercisesViewData(
                MutableLiveData(
                        day.exercises.map { workoutExercise ->
                            Pair( workoutExercise, Color(GetCategoryIconTint(workoutExercise.exercise, knownExerciseService)))
                        }
                )
            ), day
        )
    }
}

class MockFetchDaysWorkoutsUseCase(val data: WorkoutExercisesViewData): FetchDaysWorkoutsUseCase {
    override fun invoke() : FetchDaysWorkoutsUseCase.Results = FetchDaysWorkoutsUseCase.MockResults(data)
}

