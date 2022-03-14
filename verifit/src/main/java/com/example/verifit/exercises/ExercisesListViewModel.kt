package com.example.verifit.exercises

import android.widget.Filter.FilterResults
import androidx.lifecycle.viewModelScope
import com.example.verifit.Exercise
import com.example.verifit.main.BaseViewModel
import kotlinx.coroutines.launch

class ExercisesListViewModel(val FetchExercisesListUseCase: FetchExercisesListUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(ExercisesListDataResult(emptyList()))
) {

    init {
        val data = FetchExercisesListUseCase()
        _viewState.value = _viewState.value.copy(ExercisesListDataResult = data)
    }

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
//            is UiAction.DateCardClicked -> viewModelScope.launch {
//                _oneShotEvents.send(OneShotEvents.GoToExercisesList(uiAction.data.workoutDay.date))
//            }2
            is UiAction.ExerciseClick -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.exercise.name))
            }
            is UiAction.OpenSearch ->_viewState.value = _viewState.value.copy(showSearch = true)

            is UiAction.Searching -> {
                _viewState.value = _viewState.value.copy(ExercisesListDataResult = ExercisesListDataResult(
                    FetchExercisesListUseCase().exercises.filter { exercise ->

                        if (uiAction.searchString.isEmpty()) {
                            true
                        } else {
                            val filterPattern: String =
                                uiAction.searchString.lowercase().trim { it <= ' ' }

                            // If search patterns is contained in exercise name then show it
                            exercise.name.lowercase().contains(filterPattern)
                        }
                    }
                ), searchingString = uiAction.searchString, showClearSearch = uiAction.searchString.isNotEmpty())
            }
            is UiAction.ClearSearch ->_viewState.value = _viewState.value.copy(ExercisesListDataResult = FetchExercisesListUseCase(), searchingString = "", showClearSearch = false)
            UiAction.ExitSearch -> _viewState.value = _viewState.value.copy(showSearch = false, ExercisesListDataResult = FetchExercisesListUseCase(), searchingString = "", showClearSearch = false)
            UiAction.StartEdit -> viewModelScope.launch {
                _oneShotEvents.send(OneShotEvents.GoToNewCustomExercise)
            }


        }
    }


}

data class ViewState(
    val ExercisesListDataResult: ExercisesListDataResult,
    val showSearch: Boolean = false,
    val showClearSearch: Boolean = false,
    val searchingString : String = ""
) {

}

sealed class UiAction{
    class ExerciseClick(val exercise: Exercise): UiAction()
    class Searching(val searchString: String) : UiAction()

    object SearchExercises : UiAction()
    object OnStart : com.example.verifit.exercises.UiAction()
    object OpenSearch : UiAction()
    object StartEdit : UiAction()
    object ClearSearch : UiAction()
    object ExitSearch : UiAction()
}
sealed class OneShotEvents{
    object GoToNewCustomExercise : OneShotEvents()

    class GoToAddExercise(val exerciseName: String): OneShotEvents()
}