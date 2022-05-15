package com.example.verifit.addexercise.composables

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.workoutservice.WorkoutService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.ArrayList

public data class AddExerciseViewState(
        val exerciseName: String?,
        val isLoading: Boolean = false,
        val clearButtonText: String = "Clear",
        val repText: String = "",
        val weightText: String = "",
        val workoutSets: LiveData<WorkoutExercise>,
        val selected : WorkoutSet? = null,
        val personalRecord:  WorkoutSet? = null,
        val commentText: String = "",
        val history: List<WorkoutExercise> = ArrayList()
) {


    companion object{

        fun initialState(date: String?, workoutService: WorkoutService, exerciseKey: String?) :  AddExerciseViewState{
            Log.d("AddExercise", "AddExerciseViewModel ${date}")
            var comment = ""
            val sets = try {
                val u = workoutService.fetchWorkoutExercise(exerciseKey, date!!)
                comment = u.value!!.comment ?: ""
                u
            } catch (e: Exception) {
                MutableLiveData(WorkoutExercise.Null())
            }

            val (reps,weight) = workoutService.calculateMaxWeight(exerciseKey)
            val maxWeightSet = workoutService.getRecordSet(exerciseKey)

            return AddExerciseViewState(
                    exerciseName = exerciseKey,
                    workoutSets = sets,
                    weightText = reps,
                    repText = weight,
                commentText = comment,
                personalRecord = maxWeightSet
            )


        }
    }
}