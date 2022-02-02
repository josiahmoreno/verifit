package com.example.verifit

import android.content.Context
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity

class TimerServiceImpl(val ctx : Context) : TimerService {
    private var countDownTimer: CountDownTimer? = null
    override var onTick: ((Long) -> Unit)? = null
    override var onFinish: (() -> Unit)? = null
    override fun getCurrentTime(): String {
        val sharedPreferences = ctx.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        val seconds = sharedPreferences.getString("seconds", "180")
        return seconds ?: "180"
    }

    override fun cancel() {
        countDownTimer!!.cancel()
    }

    override fun save(seconds: String) {
        val sharedPreferences = ctx.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
            // Save to shared preferences
            editor.putString("seconds", seconds)
            editor.apply()
    }

    override fun start(timeLeftInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(MillisUntilFinish: Long) {
                this@TimerServiceImpl.onTick?.invoke(MillisUntilFinish)

            }

            override fun onFinish() {
                this@TimerServiceImpl.onFinish?.invoke()
            }
        }.start()
    }


}
