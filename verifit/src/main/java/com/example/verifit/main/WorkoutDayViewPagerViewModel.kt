package com.example.verifit.main

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewModelScope
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import kotlinx.coroutines.launch

class WorkoutDayViewPagerViewModel(val FetchViewPagerDataUseCase: FetchViewPagerDataUseCase)
    : BaseViewModel<ViewState,UiAction,OneShotEvents>(
            initialViewState = ViewState(FetchViewPagerDataResult(emptyList()), -1)
) {

    init {
        val data = FetchViewPagerDataUseCase()
        val selected = (data.workDays.size + 1) / 2
        _viewState.value = viewState.value.copy(FetchViewPagerDataResult = data, pageSelected =  selected)
    }

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.DateCardClicked -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.GoToExercisesList(uiAction.workDay.date))
            }
            UiAction.GoToTodayClicked ->
                viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.ScrollToPage(viewState.value.pageSelected))
                }
            is UiAction.SetClicked -> TODO()
            is UiAction.WorkoutExerciseClicked -> TODO()
        }
    }


}

data class ViewState(
       val FetchViewPagerDataResult : FetchViewPagerDataResult,
       val pageSelected : Int
)
sealed class UiAction{
    class WorkoutExerciseClicked(val workoutExercise: WorkoutExercise) : UiAction()
    class SetClicked(val workoutSet: WorkoutSet) : UiAction()
    class DateCardClicked(val workDay: WorkoutDay) : UiAction()
    object GoToTodayClicked : UiAction()

}
sealed class OneShotEvents{
    class ScrollToPage(val pageSelected: Int): OneShotEvents()
    class GoToExercisesList(val dateString: String) : OneShotEvents()
}
