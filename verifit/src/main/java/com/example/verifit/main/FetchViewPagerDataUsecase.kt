package com.example.verifit.main

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Period
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.verifit.ColorGetter
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.workoutservice.WorkoutService
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class FetchViewPagerDataUseCase(private val workoutService: WorkoutService,
                                private val colorGetter: ColorGetter) {

    operator fun invoke(): FetchViewPagerDataResult = generatedInfiniteWorkday4()


    private fun generatedInfiniteWorkday4() : FetchViewPagerDataResult {
        //return FetchViewPagerDataResult(arrayListOf<SingleViewPagerScreenData>())
        // Skip creation of empty workouts if you don't have to

        //need to fetch all the workout days from the database
        val fetchedWorkoutDays = workoutService.fetchWorkoutDays()
        val tempMap = HashMap<String, WorkoutDay>()
        for (fetchedWorkoutDay in fetchedWorkoutDays) {
            tempMap.put(fetchedWorkoutDay.date, fetchedWorkoutDay)
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dayFormatter = DateTimeFormatter.ofPattern("EEEE")
        val longDateFormatter = DateTimeFormatter.ofPattern("MMMM dd yyyy")
        // Skip creation of empty workouts if you don't have to
            // "Infinite" Data Structure


            val now =  LocalDate.now()
            val yester = now.minusYears(5)
            val tmmr = now.plusYears(5)
            // Find start and End Dates
            val betw = ChronoUnit.DAYS.between(yester, tmmr)
            val singles = arrayListOf<SingleViewPagerScreenData>()
            for(i in 0 until betw) {

                // Get Date in String format
                    val date0 = yester.plusDays(i)
                val date_str = date0.format(formatter)

                // Create new mostly empty object
                val dayExists = tempMap.containsKey(date_str)
                var map = false
                val today  = if(dayExists){
                    map = true
                    tempMap[date_str]!!
                } else {
                    WorkoutDay()
                }
                today.date = date_str

                val nameOfDayString = dayFormatter.format(date0)
                val monthDateYearString = longDateFormatter.format(date0)


                    val day = workoutService.fetchDayLive(today.date).map{ liveDay ->
                        liveDay.exercises.map { workoutExercise ->
                            Log.d("ViewPagerCompose.FetchViewPagerDataUseCase","$date_str mapping...")
                            Pair(ExerciseLiveData(workoutService.fetchWorkoutExercise(workoutExercise.exercise,workoutExercise.date)), Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
                        }
                    }
                //day.observeForever {  }

                if(map)
                Log.d("ViewPagerCompose.FetchViewPagerDataUseCase","$date_str live.value = ${day.value}")
                singles.add(SingleViewPagerScreenData(
                        exercisesViewData = WorkoutExercisesViewData(
                                day
                        ),
                        day = nameOfDayString,
                        date = monthDateYearString,
                        workoutDay = today
                ))
                //day.removeObserver {  }
            }

        return FetchViewPagerDataResult(singles)


    }

}
