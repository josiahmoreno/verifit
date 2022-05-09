package com.example.verifit.diary.workoutexercisestats

import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToAddExerciseUseCase
import com.example.verifit.common.NoOpNavigateToDayActivityUseCase
import com.example.verifit.diary.CalculatedDiaryEntryUseCase
import com.example.verifit.diary.CalculatedExerciseEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService

class WorkoutExerciseStatsViewModel(
    val exerciseName: String,
    val date: String,
    CalculatedExerciseEntryUseCase: CalculatedExerciseEntryUseCase,
    val NavigateToAddExerciseUseCase: NavigateToAddExerciseUseCase = NoOpNavigateToAddExerciseUseCase()
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(CalculatedExerciseEntryUseCase(exerciseName,date))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.EditExercise -> NavigateToAddExerciseUseCase(exerciseName,date = date)
        }
        //TODO("Not yet implemented")
    }

}

data class ViewState(
    val data: DialogData
)

sealed class UiAction{

    object EditExercise : UiAction()

}
sealed class OneShotEvents{


}