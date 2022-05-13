package com.example.verifit.diary.day

import androidx.lifecycle.SavedStateHandle
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToDayActivityUseCase
import com.example.verifit.diary.CalculatedDiaryEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiaryDayStatsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    workoutService: WorkoutService,
    val CalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase,
    val NavigateToDayUseCase: NavigateToDayActivityUseCase = NoOpNavigateToDayActivityUseCase()
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(CalculatedDiaryEntryUseCase(workoutService.fetchDay(savedStateHandle.date!!)))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            UiAction.ViewDayClick -> NavigateToDayUseCase(date = savedStateHandle.date!!)
        }
        //TODO("Not yet implemented")
    }

}

data class ViewState(
    val data: DialogData
)

sealed class UiAction{
    object ViewDayClick : UiAction()

}
sealed class OneShotEvents{


}