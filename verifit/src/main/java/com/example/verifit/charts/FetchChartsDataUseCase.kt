package com.example.verifit.charts

import android.graphics.Color
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.function.Consumer


interface FetchChartsDataUseCase{
    operator fun invoke() : Results


    interface Results {
        val data: PieData
    }
    data class ResultsImpl(
            override val data : PieData,
            //val day: WorkoutDay
    ): Results
    data class MockResults(
        override val data: PieData,
    ): Results

}
class FetchChartsDataUseCaseImpl(
    val workoutService: WorkoutService,


    ) : FetchChartsDataUseCase{

    override operator fun invoke(): FetchChartsDataUseCase.Results = fetch()

    private fun fetch(): FetchChartsDataUseCase.Results {
        val workoutDays = workoutService.fetchWorkoutDays()

        // Find Workout Years
        val Years = HashSet<String>()

        for (i in workoutDays.indices) {
            val date: String = workoutDays[i].date
            val year = date.substring(0, 4)
            Years.add(year)
        }

        // Workout years and number of workouts per year

        // Workout years and number of workouts per year
        val Years_Workouts = HashMap<String, Int>()

        // Iterate set years

        // Iterate set years
        Years.forEach{ year: String ->
            Years_Workouts[year] = 0
        }

        // Calculate number of workouts per year

        // Calculate number of workouts per year
        for (i in workoutDays.indices) {
            val date: String = workoutDays.get(i).getDate()
            val year = date.substring(0, 4)
            val workouts = Years_Workouts[year]!!
            Years_Workouts[year] = workouts + 1
        }
        val yValues = ArrayList<PieEntry>()

        for (stringIntegerEntry in Years_Workouts.entries) {
            val (key, value) = stringIntegerEntry
            val workouts = value
            val year = key
            yValues.add(PieEntry(workouts.toFloat(), year))
        }

        val pieDataSet = PieDataSet(yValues, "")

        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        pieDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)


        val data = PieData(pieDataSet)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)




        return FetchChartsDataUseCase.ResultsImpl(
            data
        )
    }

    private fun fetch2(): FetchChartsDataUseCase.Results {
        val workoutDays = workoutService.fetchWorkoutDays()

        // Find Workout Years

        // Find Workout Years
        val Bodyparts = java.util.HashSet<String>()

        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Exercise = workoutDays[i].sets[j].category
                Bodyparts.add(Exercise)
            }
        }

        // Workout years and number of workouts per year

        // Workout years and number of workouts per year
        val Number_Bodyparts = java.util.HashMap<String, Int>()

        // Iterate set years

        // Iterate set years
        Bodyparts.forEach(Consumer { exercise: String ->
            Number_Bodyparts[exercise] = 0
        })

        // Calculate number of workouts per year

        // Calculate number of workouts per year
        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Exercise = workoutDays[i].sets[j].category
                val Exercise_Workouts = Number_Bodyparts[Exercise]!!
                Number_Bodyparts[Exercise] = Exercise_Workouts + 1
            }
        }
        val yValues = java.util.ArrayList<PieEntry>()

        for (stringIntegerEntry in Number_Bodyparts.entries) {
            val (key, value) = stringIntegerEntry
            val workouts = value as Int
            val year = key as String
            yValues.add(PieEntry(workouts.toFloat(), year))
        }

        val pieDataSet = PieDataSet(yValues, "")

        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        pieDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)


        val data = PieData(pieDataSet)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)




        return FetchChartsDataUseCase.ResultsImpl(
            data
        )
    }
}

//class MockFetchChartsDataUseCase(): FetchChartsDataUseCase {
//    override fun invoke() : FetchChartsDataUseCase.Results = FetchChartsDataUseCase.MockResults()
//}

