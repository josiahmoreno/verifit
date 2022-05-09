package com.example.verifit.comment

import androidx.navigation.NavHostController
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService


class CommentViewModel(
    val navHostController: NavHostController,
    val exerciseKey: String,
    val date: String,
    val workoutService: WorkoutService,
    val comment: String?
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(workoutService.getExercise(exerciseKey)?.comment ?: comment ?: "" )
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.ClearAction -> {
                workoutService.updateComment(date, exerciseKey ,"")
                navHostController.previousBackStackEntry?.savedStateHandle?.set("comment", "")
            }
            is UiAction.SaveAction -> {
                workoutService.updateComment(date, exerciseKey ,uiAction.text)
                navHostController.previousBackStackEntry?.savedStateHandle?.set("comment", uiAction.text)
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