package com.example.verifit.timer.alarm

import android.content.Context
import android.util.Log
import com.example.verifit.timer.TimerServiceWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import io.joon.notificationtimer.NotificationTimer
import io.joon.notificationtimer.TimerService
import io.joon.notificationtimer.TimerState


class NotificationAlarmService(@ApplicationContext val context: Context,

): TimerServiceWrapper {
    init {
        Log.d("NotificationAlarmService", "init")
    }
    override fun cancel() {
        _seconds = defaultTime
        if(::notification.isInitialized){
            notification.stop()
            notification.terminate()
        }
    }

    private lateinit var notification: NotificationTimer.Builder
    val defaultTime = "180"
    var _seconds = defaultTime
    override val seconds: String
        get() = _seconds
    var _onTickString: ((String) -> Unit)? = null
    override var onTickString: ((String) -> Unit)?
        get() = _onTickString
        set(value) {
            _onTickString = value
        }
    var _onFinish: (() -> Unit)? = null
    override var onFinish: (() -> Unit)?
        get() = _onFinish
        set(value) {
            _onFinish = value
        }

    override fun start(vibrate: Boolean, sound: Boolean, autoStart: Boolean) {
        if(seconds == "0" || seconds.isBlank()){
            return
        }
        if(TimerService.state == TimerState.PAUSED) {
            Log.d("notification","timer was running")
           notification.stop()
        }
        notification = NotificationTimer.Builder(context)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setOnTickListener {
                val minutesUntilFinished = (it / 1000) / 60
                val secondsInMinuteUntilFinished = ((it / 1000) - minutesUntilFinished * 60)
                val seconds = ((it / 1000))
//                val secondsStr = secondsInMinuteUntilFinished.toString()
                val secondsStr = seconds.toString()
//                val showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
                val showTime = secondsStr
                _seconds = showTime
                Log.d("notification"," tick $_seconds")
                onTickString?.invoke(showTime)
            }
            .vibration(vibrate)
            .vibrationDuration(3000L)
            .beep(sound)
            .beepDuration(3000L)
            .setOnFinishListener {
                notification.terminate()
                _seconds = defaultTime
                _onFinish?.invoke()
            }
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)

        Log.d("notification","$seconds")
        notification.play(seconds.toLong()*1000)

    }

    override fun pause() {
        if(TimerService.state != TimerState.TERMINATED) {
            notification.stop()
        }
    }

    override fun ResetTimer(): String {
        if(TimerService.state != TimerState.TERMINATED){
            notification.stop()
        }

        _seconds = defaultTime
        return _seconds
    }

    override fun Increment(secondText: String): String {
        if(TimerRunning){

        } else {
            var newSeconds = secondText.toDouble()
            newSeconds += 1
            if (newSeconds < 0) {
                newSeconds = 0.0
            }
            _seconds = newSeconds.toInt().toString()
        }
        return seconds
    }
    override fun Decrement(secondText: String): String {
        if(TimerRunning){

        } else {
            var newSeconds = secondText.toDouble()
            newSeconds -= 1
            if (newSeconds < 0) {
                newSeconds = 0.0
            }
            _seconds = newSeconds.toInt().toString()
        }
        return seconds
    }

    override fun ChangeTime(text: String) {
        if(!TimerRunning){
            _seconds = text
        }
    }

    override var TimerRunning: Boolean
        get() = TimerService.state == TimerState.RUNNING
        set(value) {}

}