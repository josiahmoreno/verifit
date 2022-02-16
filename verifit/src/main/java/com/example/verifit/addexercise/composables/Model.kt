package com.example.verifit.addexercise.composables

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.WorkoutSet

class Model {

    val _secondsLiveData: MutableLiveData<String> = MutableLiveData()
    val secondsLiveData: LiveData<String> = _secondsLiveData
    var START_TIME_IN_MILLIS: Long = 0L
    var TimeLeftInMillis: Long = 0L
    var TimerRunning: Boolean = false
    var ExerciseComment: String = ""
    var WeightText: String = ""
    var RepText: String = ""
    var ClickedSet: WorkoutSet? = null
    val Todays_Exercise_Sets: ArrayList<WorkoutSet> = ArrayList()
}
