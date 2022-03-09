package com.example.verifit.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.example.verifit.KnownExerciseService
import com.example.verifit.WorkoutDay
import com.example.verifit.workoutservice.WorkoutService
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FetchViewPagerDataUseCase(private val workoutService: WorkoutService, private val knownExerciseService: KnownExerciseService) {
    private val infiniteWorkoutDays : MutableList<WorkoutDay> = mutableListOf()
    operator fun invoke(): FetchViewPagerDataResult = generatedInfiniteWorkday()

    private fun generatedInfiniteWorkday() : FetchViewPagerDataResult {
        // Skip creation of empty workouts if you don't have to

        //need to fetch all the workout days from the database
        val fetchedWorkoutDays = workoutService.fetchWorkoutDays()
        val tempMap = HashMap<String, WorkoutDay>()
        for (fetchedWorkoutDay in fetchedWorkoutDays) {
            tempMap.put(fetchedWorkoutDay.date, fetchedWorkoutDay)
        }

        // Skip creation of empty workouts if you don't have to
        if (infiniteWorkoutDays.isEmpty()) {
            // "Infinite" Data Structure
            infiniteWorkoutDays.clear()

            // Find start and End Dates
            val c = Calendar.getInstance()
            c.time = Date()
            c.add(Calendar.YEAR, -5)
            val startDate = c.time
            c.add(Calendar.YEAR, +10)
            val endDate = c.time

            // Create Calendar Objects that represent start and end date
            val start = Calendar.getInstance()
            start.time = startDate
            val end = Calendar.getInstance()
            end.time = endDate

            // Construct 20 years worth of empty workout days
            var date = start.time
            while (start.before(end)) {

                // Get Date in String format
                val date_str = SimpleDateFormat("yyyy-MM-dd").format(date)

                // Create new mostly empty object
                val today = WorkoutDay()
                today.date = date_str
                infiniteWorkoutDays.add(
                        if(tempMap.containsKey(date_str)){
                            tempMap.getValue(date_str)
                        } else {
                            today
                        }
                )
                start.add(Calendar.DATE, 1)
                date = start.time
            }
        }
        var count = 0
        val singleViewPagerScreenData = infiniteWorkoutDays.map { workoutDay ->
            count++
            val  dateString = workoutDay.date
            val date1 = SimpleDateFormat("yyyy-MM-dd").parse(dateString) //potential exception

            val nameOfDayDateFormat: DateFormat = SimpleDateFormat("EEEE")
            val monthDateYearFormat: DateFormat = SimpleDateFormat("MMMM dd yyyy")
            val nameOfDayString = nameOfDayDateFormat.format(date1)
            val monthDateYearString = monthDateYearFormat.format(date1)
            SingleViewPagerScreenData(
                    exercisesViewData = WorkoutExercisesViewData(
                        MutableLiveData(
                            workoutDay.exercises.map { workoutExercise ->
                                Pair( workoutExercise, Color(GetCategoryIconTint(workoutExercise.exercise)))
                            }
                        )
                    ),
                    day = nameOfDayString,
                    date = monthDateYearString,
                    workoutDay = workoutDay
            )
        }
        return FetchViewPagerDataResult(singleViewPagerScreenData)


    }

    fun GetCategoryIconTint(exercise_name: String?) : Int {
        val exercise_category = knownExerciseService.fetchExerciseCategory(exercise_name)
        when (exercise_category) {
            "Shoulders" -> {
                return android.graphics.Color.argb(255,
                    0,
                    116,
                    189) // Primary Color
            }
            "Back" -> {
                return android.graphics.Color.argb(255, 40, 176, 192)
            }
            "Chest" -> {
                return android.graphics.Color.argb(255, 92, 88, 157)
            }
            "Biceps" -> {
                return android.graphics.Color.argb(255, 255, 50, 50)
            }
            "Triceps" -> {
                return android.graphics.Color.argb(255, 204, 154, 0)
            }
            "Legs" -> {
                return android.graphics.Color.argb(255, 212, 25, 97)
            }
            "Abs" -> {
                return android.graphics.Color.argb(255, 255, 153, 171)
            }
            else -> {
                return android.graphics.Color.argb(255, 52, 58, 64) // Grey AF
            }
        }
    }
}
