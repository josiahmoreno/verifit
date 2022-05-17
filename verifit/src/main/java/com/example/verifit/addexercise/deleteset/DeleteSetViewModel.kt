package com.example.verifit.addexercise.deleteset

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToAddExerciseUseCase

import com.example.verifit.diary.CalculatedExerciseEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private val SavedStateHandle.setIdentifier: String?
    get() {
            return get("identifier")
    }

@HiltViewModel
class DeleteSetViewModel @Inject constructor(
    val savedStateHandle : SavedStateHandle,
    val DeleteSetUseCase: DeleteSetUseCase
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState()
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.DeleteSet -> DeleteSetUseCase(savedStateHandle.setIdentifier!!)
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