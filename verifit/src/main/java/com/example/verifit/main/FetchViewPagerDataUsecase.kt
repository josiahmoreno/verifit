package com.example.verifit.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Period
import androidx.lifecycle.MutableLiveData
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
    private val infiniteWorkoutDays : MutableList<WorkoutDay> = mutableListOf()
    operator fun invoke(): FetchViewPagerDataResult = generatedInfiniteWorkday4()

    private fun generatedInfiniteWorkday() : FetchViewPagerDataResult {
        // Skip creation of empty workouts if you don't have to

        //need to fetch all the workout days from the database
        val fetchedWorkoutDays = workoutService.fetchWorkoutDays()
        val tempMap = HashMap<String, WorkoutDay>()
        for (fetchedWorkoutDay in fetchedWorkoutDays) {
            tempMap.put(fetchedWorkoutDay.date, fetchedWorkoutDay)
        }

        // Skip creation of empty workouts if you don't have to
        val defaultWorkoutFormat = SimpleDateFormat("yyyy-MM-dd")
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
                val date_str = defaultWorkoutFormat.format(date)

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
        val dayFormat = SimpleDateFormat("EEEE")
        val longdateFormat = SimpleDateFormat("MMMM dd yyyy")
        val singleViewPagerScreenData = infiniteWorkoutDays.map { workoutDay ->
            count++
            val  dateString = workoutDay.date
            val date1 = defaultWorkoutFormat.parse(dateString) //potential exception

            val nameOfDayDateFormat: DateFormat = dayFormat
            val monthDateYearFormat: DateFormat = longdateFormat
            val nameOfDayString = nameOfDayDateFormat.format(date1)
            val monthDateYearString = monthDateYearFormat.format(date1)
            SingleViewPagerScreenData(
                    exercisesViewData = WorkoutExercisesViewData(
                        MutableLiveData(
                            workoutDay.exercises.map { workoutExercise ->
                                Pair( workoutExercise, Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
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

    private fun generatedInfiniteWorkday2() : FetchViewPagerDataResult {
        // Skip creation of empty workouts if you don't have to

        //need to fetch all the workout days from the database
        val fetchedWorkoutDays = workoutService.fetchWorkoutDays()
        val tempMap = HashMap<String, WorkoutDay>()
        for (fetchedWorkoutDay in fetchedWorkoutDays) {
            tempMap.put(fetchedWorkoutDay.date, fetchedWorkoutDay)
        }
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // Skip creation of empty workouts if you don't have to
        if (infiniteWorkoutDays.isEmpty()) {
            // "Infinite" Data Structure
            infiniteWorkoutDays.clear()

            var now =  LocalDate.now()
            var yester = now.minusYears(5)
            var tmmr = now.plusYears(5)
            // Find start and End Dates
            val betw = ChronoUnit.DAYS.between(yester, tmmr)

            for(i in 0..betw) {

                // Get Date in String format
                val date_str = yester.plusDays(i).format(formatter)

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
            }
        }


        var count = 0
        //val dayFormat = SimpleDateFormat("EEEE")
        //val longdateFormat = SimpleDateFormat("MMMM dd yyyy")
        var dayFormatformatter = DateTimeFormatter.ofPattern("EEEE")
        var longdateFormatformatter = DateTimeFormatter.ofPattern("MMMM dd yyyy")
        val singleViewPagerScreenData = infiniteWorkoutDays.map { workoutDay ->
            count++
            val  dateString = workoutDay.date
            val date1 = formatter.parse(dateString) //potential exception

            //val nameOfDayDateFormat: DateFormat = dayFormat
            //val monthDateYearFormat: DateFormat = longdateFormat
            val nameOfDayString = dayFormatformatter.format(date1)
            val monthDateYearString = longdateFormatformatter.format(date1)
            SingleViewPagerScreenData(
                    exercisesViewData = WorkoutExercisesViewData(
                            MutableLiveData(
                                    workoutDay.exercises.map { workoutExercise ->
                                        Pair( workoutExercise, Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
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

    private fun generatedInfiniteWorkday3() : FetchViewPagerDataResult {
        // Skip creation of empty workouts if you don't have to

        //need to fetch all the workout days from the database

        val tempMap = HashMap<String, Long>()
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        // Skip creation of empty workouts if you don't have to
        if (infiniteWorkoutDays.isEmpty()) {
            // "Infinite" Data Structure
            infiniteWorkoutDays.clear()

            var now =  LocalDate.now()
            var yester = now.minusYears(5)
            var tmmr = now.plusYears(5)
            // Find start and End Dates
            val betw = ChronoUnit.DAYS.between(yester, tmmr)

            for(i in 0..betw) {

                // Get Date in String format
                val date_str = yester.plusDays(i).format(formatter)

                // Create new mostly empty object
                val today = WorkoutDay()
                today.date = date_str
                tempMap.put(today.date, i)
                infiniteWorkoutDays.add(today)
            }
        }

        val fetchedWorkoutDays = workoutService.fetchWorkoutDays()
        var count = 0
        //val dayFormat = SimpleDateFormat("EEEE")
        //val longdateFormat = SimpleDateFormat("MMMM dd yyyy")
        var dayFormatformatter = DateTimeFormatter.ofPattern("EEEE")
        var longdateFormatformatter = DateTimeFormatter.ofPattern("MMMM dd yyyy")
        fetchedWorkoutDays.forEach {
            if(tempMap.containsKey(it.date)){
                infiniteWorkoutDays[tempMap[it.date]!!.toInt()] = it
            }
        }
        val singleViewPagerScreenData = infiniteWorkoutDays.map { workoutDay ->
            count++
            val  dateString = workoutDay.date
            val date1 = formatter.parse(dateString) //potential exception

            //val nameOfDayDateFormat: DateFormat = dayFormat
            //val monthDateYearFormat: DateFormat = longdateFormat
            val nameOfDayString = dayFormatformatter.format(date1)
            val monthDateYearString = longdateFormatformatter.format(date1)
            SingleViewPagerScreenData(
                    exercisesViewData = WorkoutExercisesViewData(
                            MutableLiveData(
                                    workoutDay.exercises.map { workoutExercise ->
                                        Pair( workoutExercise, Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
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
                val today  = if(tempMap.containsKey(date_str)){
                    tempMap[date_str]!!
                } else {
                    WorkoutDay()
                }
                today.date = date_str

                //val date1 = formatter.parse(date_str) //potential exception

                //val nameOfDayDateFormat: DateFormat = dayFormat
                //val monthDateYearFormat: DateFormat = longdateFormat
                val nameOfDayString = dayFormatter.format(date0)
                val monthDateYearString = longDateFormatter.format(date0)


                singles.add(SingleViewPagerScreenData(
                        exercisesViewData = WorkoutExercisesViewData(
                                MutableLiveData(
                                        today.exercises.map { workoutExercise ->
                                            Pair(workoutExercise, Color(colorGetter.getCategoryIconTint(workoutExercise.exercise)))
                                        }
                                )
                        ),
                        day = nameOfDayString,
                        date = monthDateYearString,
                        workoutDay = today
                ))
            }

        return FetchViewPagerDataResult(singles)


    }

}
