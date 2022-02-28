package com.example.verifit.main

import androidx.compose.foundation.layout.PaddingValues
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet

class WorkoutDayViewPagerViewModel: BaseViewModel<ViewState,UiAction,OneShotEvents>(
        ViewState()
) {
    override fun onAction(uiAction: UiAction) {
        TODO("Not yet implemented")
    }


}

data class ViewState(

)
sealed class UiAction{
    class WorkoutExerciseClicked(val workoutExercise: WorkoutExercise) : UiAction()
    class SetClicked(workoutSet: WorkoutSet) : UiAction()
    object DateCardClicked : UiAction()
    object GoToTodayClicked : UiAction()

}
sealed class OneShotEvents{

}
