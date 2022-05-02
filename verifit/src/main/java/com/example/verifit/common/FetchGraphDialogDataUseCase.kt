package com.example.verifit.common

import android.graphics.Color
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.ArrayList

interface FetchGraphDialogDataUseCase {

    operator fun invoke(exerciseName: String): LineData {
        return LineData()
    }
}

class FetchGraphDialogDataUseCaseImpl(val workoutService: WorkoutService): FetchGraphDialogDataUseCase {

    override fun invoke(exerciseName: String): LineData {
        // Create Array List that will hold graph data
        val Volume_Values = ArrayList<Entry>()
        var x = 0

        val workoutDays = workoutService.fetchWorkoutDays()
        // Get Exercise Volume
        for (i in workoutDays.indices) {
            for (j in workoutDays[i].exercises.indices) {
                val current_exercise = workoutDays[i].exercises[j]
                if (current_exercise.exercise == exerciseName) {
                    Volume_Values.add(Entry(x.toFloat(), current_exercise.volume.toFloat()))
                    x++
                }
            }
        }
        val volumeSet = LineDataSet(Volume_Values, "Volume")
        val data = LineData(volumeSet)
        volumeSet.lineWidth = 2f
        volumeSet.valueTextSize = 10f
        volumeSet.valueTextColor = Color.BLACK
        return data
    }
}

class NoOpFetchGraphDialogDataUseCase: FetchGraphDialogDataUseCase
