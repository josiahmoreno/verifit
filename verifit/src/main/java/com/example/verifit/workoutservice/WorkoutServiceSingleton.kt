package com.example.verifit

import android.content.Context
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.FakeWorkoutService2
import com.example.verifit.workoutservice.PrefWorkoutServiceImpl
import com.example.verifit.workoutservice.WorkoutService

object WorkoutServiceSingleton {
    private var WorkoutService : WorkoutService? = null
    fun getWorkoutService(context: Context): WorkoutService {
        if(WorkoutService == null){
            WorkoutService = FakeWorkoutService2(DateSelectStore)
            //WorkoutService = PrefWorkoutServiceImpl(context,DateSelectStore, KnownExerciseServiceSingleton.getKnownExerciseService(context = context))
        }
        return WorkoutService!!
    }

}