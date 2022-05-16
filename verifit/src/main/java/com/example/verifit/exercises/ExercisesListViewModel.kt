package com.example.verifit.exercises

import androidx.lifecycle.SavedStateHandle
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.GoToNewCustomExerciseCase
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExercisesListViewModel @Inject constructor(
    val FetchExercisesListUseCase: FetchExercisesListUseCase,
    val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val GoToNewCustomExerciseCase: GoToNewCustomExerciseCase,
    val savedStateHandle: SavedStateHandle? = null
)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(ExercisesListDataResult(emptyList()),ExerciseListResult2.Category(
        emptyList()))
) {
    val date = savedStateHandle?.date
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
            is UiAction.CategoryClick ->  {
                _viewState.value = _viewState.value.copy(ExercisesListDataResult = ExercisesListDataResult(uiAction.exercise.items
                ))
            }
            is UiAction.ExerciseClick -> {
                GoToAddExerciseUseCase(uiAction.item.name, _date)
            }
            //
//            is UiAction.ExerciseClick -> viewModelScope.launch {
//                _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.exercise.name))
//            }
            is UiAction.OpenSearch ->_viewState.value = _viewState.value.copy(showSearch = true)

            is UiAction.Searching -> {
                _viewState.value = _viewState.value.copy(ExercisesListDataResult = ExercisesListDataResult(
                    FetchExercisesListUseCase().results.filter { exercise ->

                        if (uiAction.searchString.isEmpty()) {
                            true
                        } else {
                            val filterPattern: String =
                                uiAction.searchString.lowercase().trim { it <= ' ' }

                            // If search patterns is contained in exercise name then show it
                            exercise.name.lowercase().contains(filterPattern)
                        }
                    }
                ,), searchingString = uiAction.searchString, showClearSearch = uiAction.searchString.isNotEmpty())
            }
            is UiAction.ClearSearch ->_viewState.value = _viewState.value.copy(ExercisesListDataResult = FetchExercisesListUseCase(), searchingString = "", showClearSearch = false)
            UiAction.ExitSearch -> _viewState.value = _viewState.value.copy(showSearch = false, ExercisesListDataResult = FetchExercisesListUseCase(), searchingString = "", showClearSearch = false)
            UiAction.StartEdit -> GoToNewCustomExerciseCase()
            UiAction.OnBackPress -> {
                if(viewState.value.searchingString.isNotBlank()){
                    _viewState.value = _viewState.value.copy(searchingString = "", showClearSearch = false, showSearch = true)
                } else if(viewState.value.searchingString.isBlank()){
                    _viewState.value = _viewState.value.copy(searchingString = "", showClearSearch = false, showSearch = false)
                } else if (viewState.value.ExerciseListResult2 is ExerciseListResult2.Exercises){
                    _viewState.value = viewState.value.copy(ExercisesListDataResult = FetchExercisesListUseCase(), searchingString = "", showClearSearch = false, showSearch =  false)
                }

            }
            UiAction.SearchExercises -> TODO()
        }
    }


}

data class ViewState(
    val ExercisesListDataResult: ExercisesListDataResult,
    val ExerciseListResult2: ExerciseListResult2,
    val showSearch: Boolean = false,
    val showClearSearch: Boolean = false,
    val searchingString : String = "",
    val isCategory : Boolean = true,
) {

}

sealed class UiAction{
    class CategoryClick(val exercise: ExerciseListResult.Category): UiAction()
    class Searching(val searchString: String) : UiAction()
    class ExerciseClick(val item: ExerciseListResult.ExerciseItem) : UiAction()

    object SearchExercises : UiAction()
    object OpenSearch : UiAction()
    object StartEdit : UiAction()
    object ClearSearch : UiAction()
    object ExitSearch : UiAction()
    object OnBackPress : UiAction()
}
sealed class OneShotEvents{


}