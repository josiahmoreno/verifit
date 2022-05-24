package com.example.verifit.timer

import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface FetchTimerViewSettings {
    operator fun invoke(): TimerViewSettings
}

class FetchTimerViewSettingsImpl constructor(): FetchTimerViewSettings {

    override fun invoke() : TimerViewSettings {
        return TimerViewSettings(true,true,true)
    }

}
data class TimerViewSettings(val vibration: Boolean, val sound : Boolean, val autoStart : Boolean)
