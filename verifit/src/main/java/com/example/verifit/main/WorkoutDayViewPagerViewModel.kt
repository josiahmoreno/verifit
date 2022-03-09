package com.example.verifit.main

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewModelScope
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.singleton.DateSelectStore
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
                _oneShotEvents.send(OneShotEvents.GoToExercisesList(uiAction.data.workoutDay.date))
            }
            UiAction.GoToTodayClicked ->
                viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.ScrollToPage(viewState.value.pageSelected))
                }
            is UiAction.SetClicked -> TODO()
            is UiAction.WorkoutExerciseClicked -> viewModelScope.launch {
                DateSelectStore.date_selected = uiAction.workoutExercise.date
                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.workoutExercise.exercise))
            }
            UiAction.OnResume -> {
                val data = FetchViewPagerDataUseCase()
                val selected = (data.workDays.size + 1) / 2
                _viewState.value = viewState.value.copy(FetchViewPagerDataResult = data, pageSelected =  selected)
            }
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
    class DateCardClicked(val data: SingleViewPagerScreenData) : UiAction()
    object GoToTodayClicked : UiAction()
    object OnResume : UiAction() {

    }

}
sealed class OneShotEvents{
    class ScrollToPage(val pageSelected: Int): OneShotEvents()
    class GoToExercisesList(val dateString: String) : OneShotEvents()
    class GoToAddExercise(val exerciseName: String): OneShotEvents()
}
