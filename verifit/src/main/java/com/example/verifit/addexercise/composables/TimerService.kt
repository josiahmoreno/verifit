package com.example.verifit.addexercise.composables

interface TimerService {
    var onFinish: (() -> Unit)?
    var onTick: ((Long) -> Unit)?


    fun getCurrentTime(): String
    fun cancel()
    fun save(seconds: String)
    fun start(timeLeftInMillis: Long)



}
