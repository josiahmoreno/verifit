package com.example.verifit.addexercise

class FakeTimer(): TimerService {
    override var onFinish: (() -> Unit)?
        get() = TODO("Not yet implemented")
        set(@Suppress("UNUSED_PARAMETER") value) {}
    override var onTick: ((Long) -> Unit)?
        get() = TODO("Not yet implemented")
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override fun getCurrentTime(): String {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun save(seconds: String) {
        TODO("Not yet implemented")
    }

    override fun start(timeLeftInMillis: Long) {
        TODO("Not yet implemented")
    }

}