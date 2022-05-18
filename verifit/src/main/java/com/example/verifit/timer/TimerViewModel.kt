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
            _viewState.value = viewState.value.copy(secondsLeft = it)
        }
        timerService.onFinish = {
            _viewState.value = viewState.value.copy(showStart = true)
        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.StartTimer -> {
                timerService.start()
                if(timerService.seconds != "0" || timerService.seconds.isNotBlank()){
                    _viewState.value = viewState.value.copy(showStart = false, showPause = true, showCancel = true)
                }

            }
            UiAction.PauseTimer -> {
                timerService.pause()
                _viewState.value = viewState.value.copy(showStart = true, showPause = true, showCancel = true)
            }
            is UiAction.DecrementSeconds -> {
                val seconds_int = timerService.Decrement(uiAction.secondText)
                _viewState.value = viewState.value.copy(secondsLeft = seconds_int)
            }
            is UiAction.IncrementSeconds -> {
                val seconds_int = timerService.Increment(uiAction.secondText)
                _viewState.value = viewState.value.copy(secondsLeft = seconds_int)
            }
            UiAction.ResetTimer -> {
                val defaultTime = timerService.ResetTimer()
                _viewState.value = viewState.value.copy(secondsLeft = defaultTime)
            }
            UiAction.OnDispose -> {
                //timerService.cancel()
            }
            is UiAction.OnTextChanged -> {
                timerService.ChangeTime(uiAction.text)
                if(!timerService.TimerRunning)
                _viewState.value = viewState.value.copy(secondsLeft = uiAction.text)
            }
            UiAction.CancelTimer -> {
                timerService.cancel()
                TODO("reset back to the exercises default time")
                timerService.ChangeTime("")
                _viewState.value = viewState.value.copy(showStart = true, showPause = false, showCancel = false)}
        }
    }
}

data class ViewState(
    val secondsLeft: String,
    val showStart: Boolean = true,
    val showPause: Boolean = false,
    val showCancel: Boolean = false
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
    object CancelTimer : UiAction()


}
sealed class OneShotEvents{


}