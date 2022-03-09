package com.example.verifit

import android.content.Context
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.PrefWorkoutServiceImpl
import com.example.verifit.workoutservice.WorkoutService

object WorkoutServiceImpl {
    private var WorkoutService : WorkoutService? = null
    fun getWorkoutService(context: Context): WorkoutService {
        if(WorkoutService == null){
            WorkoutService = PrefWorkoutServiceImpl(context,DateSelectStore)
        }
        return WorkoutService!!
    }

}