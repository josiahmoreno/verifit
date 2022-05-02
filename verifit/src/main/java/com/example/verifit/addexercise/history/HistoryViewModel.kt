package com.example.verifit.addexercise.history


import com.example.verifit.WorkoutExercise
import com.example.verifit.common.ShowExerciseStatsUseCase
import com.example.verifit.common.ShowSetStatsUseCase
import com.example.verifit.main.BaseViewModel

class HistoryViewModel(val exerciseName: String, val FetchHistoryUseCase: FetchHistoryUseCase, val ShowExerciseStatsUseCase: ShowExerciseStatsUseCase, val ShowSetStatsUseCase: ShowSetStatsUseCase) : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    ViewState(exerciseName = exerciseName,FetchHistoryUseCase(exerciseName))) {
    override fun onAction(uiAction: UiAction) {

        when(uiAction){
            UiAction.ExerciseClick -> ShowExerciseStatsUseCase()
            UiAction.SetClick -> ShowSetStatsUseCase()
        }
    }
}


data class ViewState(
    val exerciseName: String?,
    val data: List<WorkoutExercise>
)

sealed class UiAction {
    object ExerciseClick : UiAction()
    object SetClick : UiAction()


}

sealed class OneShotEvents {

}