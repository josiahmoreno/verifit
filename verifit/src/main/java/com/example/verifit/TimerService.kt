package com.example.verifit

interface TimerService {
    fun GetCurrentTime(): String
    fun cancel()
    fun save(seconds: String)
    fun start()

}
