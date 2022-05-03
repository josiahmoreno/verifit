package com.example.verifit.timer

import android.content.Context
import android.os.Debug
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.verifit.addexercise.composables.TimerService
import java.util.*

interface TimerServiceWrapper {
    fun cancel()

    val seconds: String

    //new timer
    var onTickString: ((String) -> Unit)?
    var onFinish: (() -> Unit)?
    fun start()
    fun pause()
    fun ResetTimer(): String
    fun Increment(secondText: String): String
    fun Decrement(secondText: String): String
    fun ChangeTime(text: String)

    var TimerRunning: Boolean
}

class TimerServiceWrapperImpl(val timerService: TimerService, val context: Context): TimerServiceWrapper{
    private var TimeLeftInMillis: Long  = 0L
    private var START_TIME_IN_MILLIS: Long = 0L
    private var defaultSeconds: String = "180"
    override var seconds: String = "180"
    override var TimerRunning: Boolean = false
    init {
        seconds = timerService.getCurrentTime()
        val oldStartTime = getStartTime()
        val actualTime = (Date().time) - oldStartTime
        val saveSeconds = seconds
        if(actualTime < seconds.toLong() * 1000L){
            Log.d("TimerServiceWrapper","restore to currently running seconds")
            TimerRunning = true
            seconds = (((seconds.toLong() * 1000L) - actualTime) / 1000L).toString()
            timerService.start(seconds.toLong() * 1000L)
        } else if(oldStartTime == -1L) {
            Log.d("TimerServiceWrapper","restore to paused seconds")
            seconds = seconds
        } else {
            Log.d("TimerServiceWrapper","set to default")
            seconds = defaultSeconds
        }
        Log.d("TimerServiceWrapper","oldStartTime = ${oldStartTime}, actualTime ${actualTime}, saveSeconds ${saveSeconds}, seconds $seconds,TimerRunning = $TimerRunning")
        timerService.onTick = {

            val secondsTick = it.toInt() / 1000
            seconds = secondsTick.toString()
            Log.d("TimerServiceWrapper","tick ${seconds}")
            timerService.save(seconds = seconds)
            onTickString?.invoke(seconds)
        }
        timerService.onFinish = {
            TimerRunning = false
            clearStartTime()
            onFinish?.invoke()
        }
    }

    private fun getStartTime(): Long {
        val sharedPreferences = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getLong("start_time", -1 )
    }

    private fun clearStartTime() {
        val sharedPreferences = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Save to shared preferences
        editor.putLong("start_time", -1 )
        editor.apply()
    }

    private var _onTickString : ((String) -> Unit)? = null
    override fun cancel() {


        if(TimerRunning){
            timerService.cancel()
            saveStartTime()
        }
        timerService.save(seconds = seconds)
    }

    override var onTickString: ((String) -> Unit)?
        get() = _onTickString
        set(value) {
            _onTickString = value
        }
    private var _onFinish : (() -> Unit)? = null
    override var onFinish: (() -> Unit)?
        get() = _onFinish
        set(value) {
            _onFinish = value
        }

    override fun start() {
        if (seconds.isNotEmpty()) {
            // Change actual values that timer uses
            START_TIME_IN_MILLIS = (seconds.toInt() * 1000).toLong()
            TimeLeftInMillis = START_TIME_IN_MILLIS

            // Save to shared preferences
            timerService.save(seconds)
        }
        timerService.start(TimeLeftInMillis)
        TimerRunning = true
        saveStartTime()
    }

    private fun saveStartTime(){
        val sharedPreferences = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Save to shared preferences
        editor.putLong("start_time", Date().time )
        editor.apply()
    }

    override fun pause() {
        clearStartTime()
        if(TimerRunning){
            timerService.cancel()
        }

        TimerRunning = false
    }

    override fun ResetTimer(): String {
        return if(!TimerRunning){
            TimeLeftInMillis = (defaultSeconds.toInt() * 1000).toLong()
            // Save to shared preferences
            seconds = (TimeLeftInMillis / 1000).toInt().toString()
            timerService.save(seconds)
            defaultSeconds
        } else {
            seconds
        }


    }

    override fun Increment(secondText: String): String {
        if(TimerRunning){

        } else {
            var newSeconds = secondText.toDouble()
            newSeconds += 1
            if (newSeconds < 0) {
                newSeconds = 0.0
            }
            seconds = newSeconds.toInt().toString()
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
            seconds = newSeconds.toInt().toString()
        }
        return seconds
    }

    override fun ChangeTime(text: String) {
        if(!TimerRunning){
            seconds = text
            timerService.save(seconds = seconds)
        }
    }

}