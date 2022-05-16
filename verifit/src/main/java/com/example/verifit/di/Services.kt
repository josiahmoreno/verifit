package com.example.verifit.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.example.verifit.*
import com.example.verifit.addexercise.composables.CountDownTimerService
import com.example.verifit.addexercise.composables.TimerService
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.timer.TimerServiceWrapper
import com.example.verifit.timer.TimerServiceWrapperImpl
import com.example.verifit.workoutservice.FakeKnownWorkoutService
import com.example.verifit.workoutservice.FakeWorkoutService2
import com.example.verifit.workoutservice.WorkoutService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
 class Services {

//    @Provides
//    fun getSavedStateHandle(@ApplicationContext context: Context, navHostController: NavHostController): SavedStateHandle {
//        return navHostController?.currentBackStackEntry?.savedStateHandle!!
//    }

    @Provides
    @Singleton
    fun getKnownExerciseService(): KnownExerciseService {
        return FakeKnownWorkoutService(
            arrayListOf(
                Exercise("FirstExerciseName","Biceps"),
                Exercise("SecondExerciseName","Glutes")
            )
        )
    }

    @Provides
    @Singleton
    fun getWorkoutService(knownExerciseService:KnownExerciseService): WorkoutService {
        return FakeWorkoutService2(DateSelectStore,knownExerciseService)
    }
    @Provides
    fun getTimerService(@ApplicationContext applicationContext: Context): TimerService {
        return  CountDownTimerService(applicationContext as Context)
    }

    @Provides
    fun getTimerServiceWrapper( timerService: TimerService,@ApplicationContext applicationContext: Context): TimerServiceWrapper {
        return  TimerServiceWrapperImpl(timerService,applicationContext)
    }
}