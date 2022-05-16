package com.example.verifit

import android.content.Context
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.FakeKnownWorkoutService
import com.example.verifit.workoutservice.FakeWorkoutService2
import com.example.verifit.workoutservice.PrefWorkoutServiceImpl
import com.example.verifit.workoutservice.WorkoutService

object WorkoutServiceSingleton {
    private var WorkoutService : WorkoutService? = null
    fun getWorkoutService(context: Context): WorkoutService {
        if(WorkoutService == null){
            WorkoutService = FakeWorkoutService2(DateSelectStore, FakeKnownWorkoutService(
                arrayListOf(
                    Exercise("FirstExerciseName","Biceps"),
                    Exercise("SecondExerciseName","Glutes")
                )
            ))
            //WorkoutService = PrefWorkoutServiceImpl(context,DateSelectStore, KnownExerciseServiceSingleton.getKnownExerciseService(context = context))
        }
        return WorkoutService!!
    }

}