package com.example.verifit.customexercise

import com.example.verifit.common.SaveNewExerciseUseCase
import com.example.verifit.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomExerciseViewModel @Inject constructor(val SaveNewExerciseUseCase : SaveNewExerciseUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(categories =  listOf<String>("Chest","Back","Soldiers","Biceps","Triceps","Legs","Abs" ))
) {

    init {

    }

    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.OnExerciseName -> _viewState.value = _viewState.value.copy(exerciseNameString = uiAction.exerciseNameString)
            is UiAction.CategorySelected ->  _viewState.value = _viewState.value.copy(selectCategory = uiAction.selection)
            UiAction.SaveNewExercise -> SaveNewExerciseUseCase(viewState.value.exerciseNameString,viewState.value.selectCategory)
        }
    }


}

data class ViewState(
    val exerciseNameString : String = "",
    val categories : List<String>,
    val selectCategory: String = "Chest"
) {


}

sealed class UiAction{
    object SaveNewExercise : UiAction()
    class OnExerciseName(val exerciseNameString: String) : UiAction()
    class CategorySelected(val selection: String) : UiAction()
    //class ExerciseClick(val exercise: Exercise): UiAction()
    //object ExitSearch : UiAction()
}
sealed class OneShotEvents{
    //class GoToAddExercise(val exerciseName: String): OneShotEvents()
}