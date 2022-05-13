package com.example.verifit.timer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.verifit.addexercise.composables.AddExerciseViewModel
import com.example.verifit.addexercise.composables.TimerService
import com.example.verifit.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(private val timerService: TimerServiceWrapper): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState(timerService.seconds, !timerService.TimerRunning)
) {

    init {
        timerService.onTickString = {
            val seconds = it.toInt() / 1000
            _viewState.value = viewState.value.copy(secondsLeft = it)
        }
        timerService.onFinish = {
            _viewState.value = viewState.value.copy(showStart = true)
        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.StartTimer -> {
                //saveSeconds(uiAction.secondText)
                //startTimer()
                timerService.start()
                _viewState.value = viewState.value.copy(showStart = false)
            }
            UiAction.PauseTimer -> {
                timerService.pause()
                _viewState.value = viewState.value.copy(showStart = true)
            }
            is UiAction.DecrementSeconds -> {
                val seconds_int = timerService.Decrement(uiAction.secondText)
                _viewState.value = viewState.value.copy(secondsLeft = seconds_int)
//                if (uiAction.secondText.isNotEmpty()) {
//                    var seconds = uiAction.secondText.toDouble()
//                    seconds -= 1
//                    if (seconds < 0) {
//                        seconds = 0.0
//                    }
//                    val seconds_int = seconds.toInt()
//                    _viewState.value = viewState.value.copy(secondsLeft = seconds_int.toString())
//                }
            }
            is UiAction.IncrementSeconds -> {
                val seconds_int = timerService.Increment(uiAction.secondText)
                _viewState.value = viewState.value.copy(secondsLeft = seconds_int)
//                if (uiAction.secondText.isNotEmpty()) {
//                    var seconds = uiAction.secondText.toDouble()
//                    seconds += 1
//                    if (seconds < 0) {
//                        seconds = 0.0
//                    }
//                    val seconds_int = seconds.toInt()
//                    _viewState.value = viewState.value.copy(secondsLeft = seconds_int.toString())
//                }
            }
            UiAction.ResetTimer -> {
                val defaultTime = timerService.ResetTimer()
                _viewState.value = viewState.value.copy(secondsLeft = defaultTime)
            }
            UiAction.OnDispose -> {
                timerService.cancel()
            }
            is UiAction.OnTextChanged -> {
                timerService.ChangeTime(uiAction.text)
                if(!timerService.TimerRunning)
                _viewState.value = viewState.value.copy(secondsLeft = uiAction.text)
            }
        }
    }
}

data class ViewState(
    val secondsLeft: String,
    val showStart: Boolean = true
) {

}

sealed class UiAction{
    class DecrementSeconds(val secondText: String):UiAction()
    class IncrementSeconds(val secondText: String):UiAction()
    class OnTextChanged(val text: String) : UiAction()

    object PauseTimer :UiAction()

    object StartTimer: UiAction()
    object ResetTimer : UiAction()
    object OnDispose : UiAction()


}
sealed class OneShotEvents{


}