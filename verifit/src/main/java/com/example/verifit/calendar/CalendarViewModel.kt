package com.example.verifit.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.example.verifit.addexercise.history.date
import com.example.verifit.main.BaseViewModel
import com.example.verifit.settings.ToastMaker
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState.initialState(savedStateHandle.date )
) {
    val date = savedStateHandle.date

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.OnDateClick -> {
                if(viewState.value.currentSelection == uiAction.localDate){
                    _viewState.value = viewState.value.copy(currentSelection = null)
                } else {
                    _viewState.value = viewState.value.copy(currentSelection = uiAction.localDate)
                }

            }
        }
    }

}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

 data class ViewState(val currentSelection: LocalDate?) {
    companion object{
        fun initialState(date: String?): ViewState {
            //TODO()
            return ViewState(currentSelection = LocalDate.parse(date, formatter))
        }
    }

}

sealed class UiAction{
   class OnDateClick(val localDate: LocalDate): UiAction()
}
sealed class OneShotEvents{


}