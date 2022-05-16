package com.example.verifit.addexercise.history


import androidx.lifecycle.SavedStateHandle
import com.example.verifit.WorkoutExercise
import com.example.verifit.common.ShowExerciseStatsUseCase
import com.example.verifit.common.ShowSetStatsUseCase
import com.example.verifit.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

val SavedStateHandle.exerciseName: String?
    get() {
       return get("exercise_name")
    }
val SavedStateHandle.date: String?
    get() {
        return get("date")
    }
val SavedStateHandle.comment: String?
    get() {
        return get("comment")
    }

@HiltViewModel
class HistoryViewModel @Inject constructor(savedStateHandle: SavedStateHandle,
                                           val FetchHistoryUseCase: FetchHistoryUseCase,
                                           val ShowExerciseStatsUseCase: ShowExerciseStatsUseCase,
                                           val ShowSetStatsUseCase: ShowSetStatsUseCase) : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    ViewState(exerciseName = savedStateHandle.exerciseName,FetchHistoryUseCase(savedStateHandle.exerciseName!!))) {
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