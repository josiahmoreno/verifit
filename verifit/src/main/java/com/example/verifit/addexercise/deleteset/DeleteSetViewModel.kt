package com.example.verifit.addexercise.deleteset

import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToAddExerciseUseCase

import com.example.verifit.diary.CalculatedExerciseEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService

class DeleteSetViewModel(
    val setIdentifier : String,
    val DeleteSetUseCase: DeleteSetUseCase
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState()
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.DeleteSet -> DeleteSetUseCase(setIdentifier)
        }
        //TODO("Not yet implemented")
    }

}

class ViewState


sealed class UiAction{


    object DeleteSet : UiAction()

}
sealed class OneShotEvents{


}