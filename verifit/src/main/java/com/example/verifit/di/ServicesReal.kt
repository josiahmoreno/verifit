//package com.example.verifit.di
//
//import android.content.Context
//import androidx.lifecycle.SavedStateHandle
//import androidx.navigation.NavHostController
//import com.example.verifit.*
//import com.example.verifit.addexercise.composables.CountDownTimerService
//import com.example.verifit.addexercise.composables.TimerService
//import com.example.verifit.singleton.DateSelectStore
//import com.example.verifit.timer.TimerServiceWrapper
//import com.example.verifit.timer.TimerServiceWrapperImpl
//import com.example.verifit.workoutservice.FakeWorkoutService2
//import com.example.verifit.workoutservice.WorkoutService
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//
//
//@InstallIn(SingletonComponent::class)
//@Module
//abstract class ServicesReal {
//
////    @Provides
////    fun getSavedStateHandle(@ApplicationContext context: Context, navHostController: NavHostController): SavedStateHandle {
////        return navHostController?.currentBackStackEntry?.savedStateHandle!!
////    }
//
//    @Provides
//    fun getKnownExerciseService(@ApplicationContext applicationContext: Context): KnownExerciseService {
//        return PrefKnownExerciseServiceImpl(applicationContext)
//    }
//    @Provides
//    fun getWorkoutService(): WorkoutService {
//        return FakeWorkoutService2(DateSelectStore)
//    }
//    @Provides
//    fun getTimerService(@ApplicationContext applicationContext: Context): TimerService {
//        return  CountDownTimerService(applicationContext as Context)
//    }
//
//    @Provides
//    fun getTimerServiceWrapper( timerService: TimerService,@ApplicationContext applicationContext: Context): TimerServiceWrapper {
//        return  TimerServiceWrapperImpl(timerService,applicationContext)
//    }
//}