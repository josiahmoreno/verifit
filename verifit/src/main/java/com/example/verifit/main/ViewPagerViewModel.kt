package com.example.verifit.main

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.trace
import androidx.lifecycle.*
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.*
import com.example.verifit.singleton.DateSelectStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    val FetchViewPagerDataUseCase: FetchViewPagerDataUseCase,
    val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val NavigateToCalendarUseCase: NavigateToCalendarUseCase,
    val NavigateToExercisesListUseCase: NavigateToExercisesListUseCase = MockNavigateToExercisesListUseCase(),
    val NavigateToSettingsUseCase: NavigateToSettingsUseCase = NoOpNavigateToSettingsUseCase(),
    val savedStateHandle: SavedStateHandle? = null,
) : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState.empty()
), IViewPagerViewModel {

    val date = savedStateHandle?.date
    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                FetchViewPagerDataUseCase().asFlow().collect { fetch ->
                    //delay(10000)
                    val data = fetch
                    //_viewState.value = viewState.value.copy(loading = false)
                    Log.d("ViewPagerViewModel.initialState", "date = $date")
                    val selected: Int = if (date == null) {
                        (data.workDays.size + 1) / 2
                    } else {
                        fetch.workDays.indexOfFirst {
                            it.workoutDay.date == date
                        }
                    }
                    Log.d("ViewPagerViewModel.initialState", "data.workDays.Size = ${data.workDays.size}, selected = $selected")
                    _viewState.value = viewState.value.copy(loading = false, FetchViewPagerDataResult = data, pageSelected = selected)
                }
            }

        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.DateCardClicked -> NavigateToExercisesListUseCase(uiAction.data.workoutDay.date)
            UiAction.GoToTodayClicked -> {
                if(!viewState.value.loading) {
                    //_viewState.value = viewState.value.copy(pageSelected = viewState.value.FetchViewPagerDataResult.workDays.size / 2)
                    viewModelScope.launch {
                        _oneShotEvents.send(OneShotEvents.ScrollToPage(viewState.value.FetchViewPagerDataResult.workDays.size / 2))
                    }
                }
            }
            is UiAction.SetClicked -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.ShowSetStats(uiAction.workoutSet))
            }
            is UiAction.WorkoutExerciseClicked -> //viewModelScope.launch
            {
                if(!viewState.value.loading) {


                    DateSelectStore.date_selected = uiAction.workoutExercise.date
                    GoToAddExerciseUseCase(uiAction.workoutExercise.exercise,
                        uiAction.workoutExercise.date)
                }
                //_oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.workoutExercise.exercise))
            }
//            UiAction.OnResume -> runBlocking(Dispatchers.IO) {
//                _viewState.value = viewState.value.copy(loading = true)
//                val fetch = async { FetchViewPagerDataUseCase() }
//
//                val data = fetch.await()
//
//                //_viewState.value = viewState.value.copy(loading = false)
//                val selected = (data.workDays.size + 1) / 2
//                _viewState.value = viewState.value.copy(loading = false,
//                    FetchViewPagerDataResult = data,
//                    pageSelected = selected)
//                Log.d("MainViewModel", "OnResume3end")
//            }
            is UiAction.StartNewExerciseClicked -> {
                if(!viewState.value.loading) {
                    NavigateToExercisesListUseCase(uiAction.workoutDay.date)
                }
            }
            is UiAction.NavigateToCalendar -> {
                if(!viewState.value.loading && uiAction.workoutDay != null) {
                    NavigateToCalendarUseCase(viewState.value.FetchViewPagerDataResult.workDays[uiAction.workoutDay].workoutDay.date)
                }
            }
            UiAction.GoToSettings -> {
                if(!viewState.value.loading) {
                    NavigateToSettingsUseCase()
                }
            }

            //NavigateToSettings
            is UiAction.GoToExercisesClicked -> {
                if(!viewState.value.loading && uiAction.currentPage != null) {
                    val workoutDay = viewState.value.FetchViewPagerDataResult.workDays[uiAction.currentPage].workoutDay
                    NavigateToExercisesListUseCase(workoutDay.date)
                }
            }

        }
    }


}

data class ViewState(
    val FetchViewPagerDataResult: FetchViewPagerDataResult,
    val pageSelected: Int,
    val loading: Boolean = false,
) {
    companion object {
            fun empty(): ViewState {
                return ViewState(FetchViewPagerDataResult(emptyList()),0,true)
            }
//        fun initialState(
//            date: String?,
//            FetchViewPagerDataUseCase: FetchViewPagerDataUseCase,
//        ): ViewState  = runBlocking{
//
//            return androidx.tracing.trace("ViewPagerViewModel.initialState") {
//
//
//                Log.d("ViewPagerViewModel.initialState", "starting... ${date}")
//                val fetch: FetchViewPagerDataResult = FetchViewPagerDataUseCase.
//
//                val data = fetch
//                //_viewState.value = viewState.value.copy(loading = false)
//                Log.d("ViewPagerViewModel.initialState", "date = $date")
//                val selected: Int = if (date == null) {
//                    (data.workDays.size + 1) / 2
//                } else {
//                    fetch.workDays.indexOfFirst {
//                        it.workoutDay.date == date
//                    }
//                }
//
//                ViewState(loading = false,
//                    FetchViewPagerDataResult = data,
//                    pageSelected = selected)
//            }
//        }
    }
}
sealed class UiAction {
    class WorkoutExerciseClicked(val workoutExercise: WorkoutExercise) : UiAction()
    class SetClicked(val workoutSet: WorkoutSet) : UiAction()
    class DateCardClicked(val data: SingleViewPagerScreenData) : UiAction()
    object GoToTodayClicked : UiAction()
    object OnResume : UiAction() {

    }

    object GoToSettings : UiAction()

    class NavigateToCalendar(val workoutDay: Int?) : UiAction()

    class StartNewExerciseClicked(val workoutDay: WorkoutDay) : UiAction()
    class GoToExercisesClicked(val currentPage: Int?) : UiAction()


}

sealed class OneShotEvents {
    class ScrollToPage(val pageSelected: Int) : OneShotEvents()
    class GoToExercisesList(val dateString: String) : OneShotEvents()
    class GoToAddExercise(val exerciseName: String) : OneShotEvents()
    class ShowSetStats(val set: WorkoutSet) : OneShotEvents()
}
