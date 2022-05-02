package com.example.verifit.timer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.addexercise.composables.TimerService
import com.example.verifit.main.BaseViewModel

class TimerViewModel(val timerService: TimerServiceWrapper): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState("180", "Start")
) {

    init {
        timerService.onTickString = {
            val seconds = it.toInt() / 1000
            _viewState.value = viewState.value.copy(secondsLeft = it)
        }
        timerService.onFinish = {
            _viewState.value = viewState.value.copy(timerButtonText = "Start")
        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.StartTimer -> {
                //saveSeconds(uiAction.secondText)
                //startTimer()
                timerService.start()
                _viewState.value = viewState.value.copy(timerButtonText = "Pause")
            }
            UiAction.PauseTimer -> {
                timerService.pause()
                _viewState.value = viewState.value.copy(timerButtonText = "Pause")
            }
            is UiAction.DecrementSeconds -> {
                if (uiAction.secondText.isNotEmpty()) {
                    var seconds = uiAction.secondText.toDouble()
                    seconds -= 1
                    if (seconds < 0) {
                        seconds = 0.0
                    }
                    val seconds_int = seconds.toInt()
                    _viewState.value = viewState.value.copy(secondsLeft = seconds_int.toString())
                }
            }
            is UiAction.IncrementSeconds -> {
                if (uiAction.secondText.isNotEmpty()) {
                    var seconds = uiAction.secondText.toDouble()
                    seconds += 1
                    if (seconds < 0) {
                        seconds = 0.0
                    }
                    val seconds_int = seconds.toInt()
                    _viewState.value = viewState.value.copy(secondsLeft = seconds_int.toString())
                }
            }
        }
    }
}

data class ViewState(
    val secondsLeft: String,
    val timerButtonText: String,
    val showStart: Boolean = true
) {

}

sealed class UiAction{
    class DecrementSeconds(val secondText: String):UiAction()
    class IncrementSeconds(val secondText: String):UiAction()
    object PauseTimer :UiAction()

    object StartTimer: UiAction()


}
sealed class OneShotEvents{


}