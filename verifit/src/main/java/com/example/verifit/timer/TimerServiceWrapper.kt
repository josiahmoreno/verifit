package com.example.verifit.timer

import com.example.verifit.addexercise.composables.TimerService

interface TimerServiceWrapper {
    //new timer
    var onTickString: ((String) -> Unit)?
    var onFinish: (() -> Unit)?
    fun start()
    fun pause()
}

class TimerServiceWrapperImpl(val timerService: TimerService): TimerServiceWrapper{
    init {
        timerService.onTick = {
            val seconds = it.toInt() / 1000
            onTickString?.invoke(seconds.toString())
        }
        timerService.onFinish = {
            onFinish?.invoke()
        }
    }

    override var onTickString: ((String) -> Unit)?
        get() = TODO("Not yet implemented")
        set(value) {}
    private var _onFinish : (() -> Unit)? = null
    override var onFinish: (() -> Unit)?
        get() = _onFinish
        set(value) {
            _onFinish = value
        }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

}