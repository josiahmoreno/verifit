package com.example.verifit

import android.content.Context
import com.example.verifit.workoutservice.WorkoutService

object KnownExerciseServiceSingleton {
    private var KnownExerciseService : KnownExerciseService? = null
    fun getKnownExerciseService(context: Context): KnownExerciseService {
        if(KnownExerciseService == null){
            KnownExerciseService =
                //com.example.verifit.PrefKnownExerciseServiceImpl(context)
                    DefaultKnownExercise()
        }
        return KnownExerciseService!!
    }

}