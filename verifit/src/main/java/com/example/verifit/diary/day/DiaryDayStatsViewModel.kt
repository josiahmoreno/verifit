package com.example.verifit.diary.day

import com.example.verifit.common.NavigateToDayActivityUseCase
import com.example.verifit.common.NoOpNavigateToDayActivityUseCase
import com.example.verifit.diary.CalculatedDiaryEntryUseCase
import com.example.verifit.diary.DialogData
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService

class DiaryDayStatsViewModel(
    val date: String,
    workoutService: WorkoutService,
    val CalculatedDiaryEntryUseCase: CalculatedDiaryEntryUseCase,
    val NavigateToDayUseCase: NavigateToDayActivityUseCase = NoOpNavigateToDayActivityUseCase()
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(CalculatedDiaryEntryUseCase(workoutService.fetchDay(date)))
) {
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            UiAction.ViewDayClick -> NavigateToDayUseCase(date = date)
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