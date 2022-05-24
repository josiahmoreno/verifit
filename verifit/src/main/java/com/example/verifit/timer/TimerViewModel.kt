package com.example.verifit.timer

import android.util.Log
import com.example.verifit.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.joon.notificationtimer.TimerState
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(private val timerService: TimerServiceWrapper, val FetchTimerViewSettings: FetchTimerViewSettings): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState.initialState(timerService,FetchTimerViewSettings)
) {

    init {
        timerService.onTickString = {
            _viewState.value = viewState.value.copy(secondsLeft = it)
        }
        timerService.onFinish = {
            _viewState.value = viewState.value.copy(showStart = true, secondsLeft = timerService.seconds)
            Log.d("TimerViewModel","timerService onFinish")
        }
    }

    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.StartTimer -> {
                timerService.start(viewState.value.vibrate,viewState.value.sound,viewState.value.autoStart)
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
                //TODO("reset back to the exercises default time")
                //timerService.ChangeTime("")
                _viewState.value = viewState.value.copy(showStart = true, showPause = false, showCancel = false, secondsLeft = timerService.seconds)}
        }
    }
}

data class ViewState(
    val secondsLeft: String,
    val showStart: Boolean = true,
    val showPause: Boolean = false,
    val showCancel: Boolean = false,
    val vibrate: Boolean,
    val sound: Boolean,
    val autoStart: Boolean
) {
    companion object {
        fun initialState(timerService: TimerServiceWrapper, FetchTimerViewSettings: FetchTimerViewSettings): ViewState{
            val settings = FetchTimerViewSettings()
            return ViewState(timerService.seconds, timerService.TimerRunning, vibrate=  settings.vibration, sound = settings.sound, autoStart =  settings.autoStart)
        }
    }
}

sealed class UiAction{
    class DecrementSeconds(val secondText: String):UiAction()
    class IncrementSeconds(val secondText: String):UiAction()
    class OnTextChanged(val text: String) : UiAction()
    class OnVibrateCheck(val vibrate: Boolean) : UiAction()
    class OnSoundChanged(val vibrate: Boolean) : UiAction()
    class OnAutoStartChanged(val vibrate: Boolean) : UiAction()

    object PauseTimer :UiAction()

    object StartTimer: UiAction()
//    class StartTimer(val vibrate: Boolean, val sound: Boolean, val autoStart: Boolean): UiAction()
    object ResetTimer : UiAction()
    object OnDispose : UiAction()
    object CancelTimer : UiAction()


}
sealed class OneShotEvents