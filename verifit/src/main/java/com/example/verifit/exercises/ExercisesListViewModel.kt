package com.example.verifit.exercises

import android.widget.Filter.FilterResults
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.verifit.Exercise
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.GoToNewCustomExerciseCase
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ExercisesListViewModel @AssistedInject constructor(
    val FetchExercisesListUseCase: FetchExercisesListUseCase,
    val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val GoToNewCustomExerciseCase: GoToNewCustomExerciseCase,
    @Assisted var date: String?
)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(ExercisesListDataResult(emptyList()))
) {
    @AssistedFactory
    interface Factory {
        fun create(date: String?): ExercisesListViewModel
    }
    companion object {
        fun provideFactory(
                assistedFactory: Factory,
                date: String?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(date) as T
            }
        }
    }

    var _date: String = if(date==null){
        DateSelectStore.date_selected
    } else {
        date!!
    }

    init {
        val data = FetchExercisesListUseCase()
        _viewState.value = _viewState.value.copy(ExercisesListDataResult = data)
    }

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
//            is UiAction.DateCardClicked -> viewModelScope.launch {
//                _oneShotEvents.send(OneShotEvents.GoToExercisesList(uiAction.data.workoutDay.date))
//            }2
            is UiAction.ExerciseClick -> GoToAddExerciseUseCase(uiAction.exercise.name, _date)
//            is UiAction.ExerciseClick -> viewModelScope.launch {
//                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.exercise.name))
//            }
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
            UiAction.StartEdit -> GoToNewCustomExerciseCase()


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
    object OpenSearch : UiAction()
    object StartEdit : UiAction()
    object ClearSearch : UiAction()
    object ExitSearch : UiAction()
}
sealed class OneShotEvents{


}