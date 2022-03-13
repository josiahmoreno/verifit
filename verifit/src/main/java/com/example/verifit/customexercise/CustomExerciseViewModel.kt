package com.example.verifit.customexercise

import androidx.lifecycle.viewModelScope
import com.example.verifit.Exercise
import com.example.verifit.exercises.ExercisesListDataResult
import com.example.verifit.exercises.FetchExercisesListUseCase
import com.example.verifit.main.BaseViewModel
import kotlinx.coroutines.launch

class CustomExerciseViewModel(val FetchExercisesListUseCase: FetchExercisesListUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(categories =  listOf<String>("Chest","Back","Soldiers","Biceps","Triceps","Legs","Abs" ))
) {

    init {

    }

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.OnExerciseName -> _viewState.value = _viewState.value.copy(exerciseNameString = uiAction.exerciseNameString)
        }
    }


}

data class ViewState(
    val exerciseNameString : String = "",
    val categories : List<String>
) {

}

sealed class UiAction{
    class OnExerciseName(val exerciseNameString: String) : UiAction()
    //class ExerciseClick(val exercise: Exercise): UiAction()
    //object ExitSearch : UiAction()
}
sealed class OneShotEvents{
    //class GoToAddExercise(val exerciseName: String): OneShotEvents()
}