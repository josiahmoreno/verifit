package com.example.verifit.singleday

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.verifit.diary.DialogData
import com.example.verifit.diary.DiaryEntry
import com.example.verifit.diary.ExerciseEntry
import com.example.verifit.diary.ExerciseEntryStats
import com.example.verifit.main.BaseViewModel

class DayViewModel()
        : BaseViewModel<ViewState, UiAction, OneShotEvents>(
            initialViewState = ViewState()
    ) {
        override fun onAction(uiAction: UiAction) {
            when (uiAction) {
                UiAction.OnResume -> {

                }
            }
        }
}


 class ViewState(

)

sealed class UiAction{
    object OnResume : UiAction()


}
sealed class OneShotEvents{
  //  class GoToAddExercise(val exerciseName: String): OneShotEvents()
}