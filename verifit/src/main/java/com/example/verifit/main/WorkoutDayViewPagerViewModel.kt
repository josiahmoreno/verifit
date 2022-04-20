package com.example.verifit.main

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewModelScope
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.singleton.DateSelectStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WorkoutDayViewPagerViewModel(val FetchViewPagerDataUseCase: FetchViewPagerDataUseCase)
    : BaseViewModel<ViewState,UiAction,OneShotEvents>(
            initialViewState = ViewState(FetchViewPagerDataResult(emptyList()), 0)
) {

    init {
        Log.d("MainViewModel","yo2")

//        val data = FetchViewPagerDataUseCase()
//        Log.d("MainViewModel","yo3")
//        val selected = (data.workDays.size + 1) / 2
//        _viewState.value = viewState.value.copy(FetchViewPagerDataResult = data, pageSelected =  selected)

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
            is UiAction.SetClicked -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.ShowSetStats(uiAction.workoutSet))
            }
            is UiAction.WorkoutExerciseClicked -> viewModelScope.launch {
                DateSelectStore.date_selected = uiAction.workoutExercise.date
                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.workoutExercise.exercise))
            }
            UiAction.OnResume -> runBlocking(Dispatchers.IO) {
                Log.d("MainViewModel","OnResume1")
                _viewState.value = viewState.value.copy(loading = true)
                val fetch =  async{FetchViewPagerDataUseCase()}

                val data = fetch.await()
                Log.d("MainViewModel","OnResume2")
                //_viewState.value = viewState.value.copy(loading = false)
                val selected = (data.workDays.size + 1) / 2
                _viewState.value = viewState.value.copy(loading = false,FetchViewPagerDataResult = data, pageSelected =  selected)
                Log.d("MainViewModel","OnResume3end")
            }
        }
    }


}

data class ViewState(
       val FetchViewPagerDataResult : FetchViewPagerDataResult,
       val pageSelected : Int,
       val loading : Boolean = true
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
    class ShowSetStats(val set: WorkoutSet): OneShotEvents()
}
