package com.example.verifit.charts

import android.graphics.Color
import com.example.verifit.ChartsActivity
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap


interface FetchChartsDataUseCase{
    operator fun invoke() : Results


    interface Results {
        val data: PieChartData
    }
    data class ResultsImpl(
            override val data : PieChartData,
            //val day: WorkoutDay
    ): Results
    data class MockResults(
        override val data: PieChartData,
    ): Results

}
data class PieChartData(
    val workoutsData: PieData,
    val bodyPartData: PieData,
    val exerciseBreakdown: PieData,
)
class FetchChartsDataUseCaseImpl(
    val workoutService: WorkoutService,


    ) : FetchChartsDataUseCase{

    override operator fun invoke(): FetchChartsDataUseCase.Results = FetchChartsDataUseCase.ResultsImpl(PieChartData(fetch(),fetch2(),fetch3()))

    private fun fetch(): PieData {
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




        return data

    }

    private fun fetch2(): PieData {
        val workoutDays = workoutService.fetchWorkoutDays()

        // Find Workout Years

        // Find Workout Years
        val Bodyparts = HashSet<String>()

        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Exercise = workoutDays[i].sets[j].category
                Bodyparts.add(Exercise)
            }
        }

        // Workout years and number of workouts per year

        // Workout years and number of workouts per year
        val Number_Bodyparts = HashMap<String, Int>()

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
        val yValues = ArrayList<PieEntry>()

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




        return data

    }

    private fun fetch3(): PieData {
        val workoutDays = workoutService.fetchWorkoutDays()

        // Find Workout Years

        // Find Workout Years
        val Exercises = HashSet<String>()

        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Exercise = workoutDays[i].sets[j].exercise
                Exercises.add(Exercise)
            }
        }

        // Workout years and number of workouts per year

        // Workout years and number of workouts per year
        val Number_Exercises = HashMap<String, Int>()

        // Iterate set years

        // Iterate set years
        Exercises.forEach(Consumer { exercise: String ->
            Number_Exercises[exercise] = 0
        })

        // Calculate number of workouts per year

        // Calculate number of workouts per year
        for (i in workoutDays.indices) {
            for (j in workoutDays[i].sets.indices) {
                val Exercise = workoutDays[i].sets[j].exercise

                //System.out.println(Exercise);
                val Exercise_Workouts = Number_Exercises[Exercise]!!
                Number_Exercises[Exercise] = Exercise_Workouts + 1
            }
        }


        val Number_Exercises_Sorted: HashMap<String, Int> =
            sortValues(Number_Exercises)


        println(Number_Exercises_Sorted.size)

        val Number_Exercises_Sorted_Pruned = HashMap<String, Int>()

        val top_exercises = 5
        var counter = 0

        for ((key, value) in Number_Exercises_Sorted) {
            counter++
            if (counter > Number_Exercises_Sorted.size - top_exercises) {
                Number_Exercises_Sorted_Pruned[key] = value
                println("$key = $value")
            }
        }


        // Initialize Pie Chart




        val yValues = ArrayList<PieEntry>()

        for (stringIntegerEntry in Number_Exercises_Sorted_Pruned.entries) {
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




        return data

    }

    private fun sortValues(map: HashMap<String, Int>): HashMap<String, Int> {
        val list = map.entries.toList()
        //Custom Comparator

        Collections.sort(list) { o1, o2 ->
            val what = o1.value
            val who = o2.value
            what.compareTo(who)
        }
        //copying the sorted list in HashMap to preserve the iteration order
        val sortedHashMap: HashMap<String, Int> = HashMap()
        val it = list.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next()
            sortedHashMap[key] = value
        }
        return sortedHashMap
    }
}

//class MockFetchChartsDataUseCase(): FetchChartsDataUseCase {
//    override fun invoke() : FetchChartsDataUseCase.Results = FetchChartsDataUseCase.MockResults()
//}

