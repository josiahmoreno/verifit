package com.example.verifit.diary.workoutexercisestats

import androidx.lifecycle.SavedStateHandle
import com.example.verifit.addexercise.history.date
import com.example.verifit.addexercise.history.exerciseName
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToAddExerciseUseCase
import com.example.verifit.common.NoOpNavigateToDayActivityUseCase
import com.example.verifit.diary.CalculatedDiaryEntryUseCase
import com.example.verifit.diary.CalculatedExerciseEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutExerciseStatsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    CalculatedExerciseEntryUseCase: CalculatedExerciseEntryUseCase,
    val NavigateToAddExerciseUseCase: NavigateToAddExerciseUseCase = NoOpNavigateToAddExerciseUseCase()
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(CalculatedExerciseEntryUseCase(savedStateHandle.exerciseName!!, savedStateHandle.date!!))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.EditExercise -> NavigateToAddExerciseUseCase(savedStateHandle.exerciseName!!,date = savedStateHandle.date!!)
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