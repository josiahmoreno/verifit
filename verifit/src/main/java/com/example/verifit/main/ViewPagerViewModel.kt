package com.example.verifit.main

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewModelScope
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.common.MockNavigateToExercisesListUseCase
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToExercisesListUseCase
import com.example.verifit.singleton.DateSelectStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ViewPagerViewModel(val FetchViewPagerDataUseCase: FetchViewPagerDataUseCase,
                         val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
                         val NavigateToExercisesListUseCase: NavigateToExercisesListUseCase = MockNavigateToExercisesListUseCase(),
                         val date : String?= null)
    : BaseViewModel<ViewState,UiAction,OneShotEvents>(
            initialViewState = ViewState.initialState(date = date,FetchViewPagerDataUseCase = FetchViewPagerDataUseCase)
) {



    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.DateCardClicked -> NavigateToExercisesListUseCase(uiAction.data.workoutDay.date)
            UiAction.GoToTodayClicked -> //_viewState.value = viewState.value.copy(pageSelected = viewState.value.FetchViewPagerDataResult.workDays.size / 2)
                viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.ScrollToPage(viewState.value.FetchViewPagerDataResult.workDays.size / 2))
                }
            is UiAction.SetClicked -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.ShowSetStats(uiAction.workoutSet))
            }
            is UiAction.WorkoutExerciseClicked -> //viewModelScope.launch
                 {
                DateSelectStore.date_selected = uiAction.workoutExercise.date
                     GoToAddExerciseUseCase(uiAction.workoutExercise.exercise,uiAction.workoutExercise.date)
                //_oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.workoutExercise.exercise))
            }
            UiAction.OnResume -> runBlocking(Dispatchers.IO) {
                _viewState.value = viewState.value.copy(loading = true)
                val fetch =  async{ FetchViewPagerDataUseCase() }

                val data = fetch.await()

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
) {
    companion object {

       fun initialState(date: String?,FetchViewPagerDataUseCase: FetchViewPagerDataUseCase): ViewState {
           val fetch: FetchViewPagerDataResult =  FetchViewPagerDataUseCase()

           val data = fetch
           //_viewState.value = viewState.value.copy(loading = false)
           Log.d("ViewPagerViewModel.initialState","date = $date")
           val selected : Int = if(date == null) {
               (data.workDays.size + 1) / 2
           } else {
               fetch.workDays.indexOfFirst {
                   it.workoutDay.date == date
               }
           }
           return ViewState(loading = false,FetchViewPagerDataResult = data, pageSelected =  selected)
       }

    }
}
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
