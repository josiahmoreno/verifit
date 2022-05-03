package com.example.verifit.comment

import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService


class CommentViewModel(val exerciseKey: String, val workoutService: WorkoutService): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(workoutService.getExercise(exerciseKey)?.comment ?: "")
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.ClearAction -> {
                workoutService.updateComment(DateSelectStore.date_selected, exerciseKey ,"")
            }
            is UiAction.SaveAction -> {
                workoutService.updateComment(DateSelectStore.date_selected, exerciseKey ,uiAction.text)
            }
        }
    }

}


data class ViewState(
    val comment: String,
) {

}

sealed class UiAction{
    class SaveAction(val text: String) : UiAction()
    class ClearAction(val text:String): UiAction()

}
sealed class OneShotEvents{


}