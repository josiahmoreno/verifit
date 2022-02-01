package com.example.verifit

class Model {

    var START_TIME_IN_MILLIS: Long = 0L
    var TimeLeftInMillis: Long = 0L
    var TimerRunning: Boolean = false
    var ExerciseComment: String = ""
    var WeightText: String = ""
    var RepText: String = ""
    var ClickedSet: WorkoutSet? = null
    val Todays_Exercise_Sets: ArrayList<WorkoutSet> = ArrayList()
}
