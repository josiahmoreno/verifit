package com.example.verifit.settings

import androidx.lifecycle.MutableLiveData
import com.example.verifit.WorkoutExercise
import com.example.verifit.main.BaseViewModel
import com.example.verifit.main.WorkoutExercisesViewData


class SettingsViewModel()
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(
        date = "Monday, April 9 3333")
) {
    override fun onAction(uiAction: UiAction) {
        TODO("Not yet implemented")
    }
}

data class ViewState(
    val date: String
)

sealed class UiAction{
    object OnResume : UiAction()

}
sealed class OneShotEvents{
    class GoToAddExercise(val exerciseName: String): OneShotEvents()
}