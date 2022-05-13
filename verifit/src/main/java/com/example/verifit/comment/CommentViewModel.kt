package com.example.verifit.comment

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.example.verifit.addexercise.history.comment
import com.example.verifit.addexercise.history.date
import com.example.verifit.addexercise.history.exerciseName
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    val navHostController: NavHostController,
    val savedStateHandle: SavedStateHandle,
    val workoutService: WorkoutService,
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(workoutService.getExercise(savedStateHandle.exerciseName)?.comment ?: savedStateHandle.comment ?: "" )
) {
    val date = savedStateHandle.date
    val exerciseKey = savedStateHandle.exerciseName

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