package com.example.verifit.addexercise.composables

import androidx.lifecycle.LiveData
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import java.util.ArrayList

public data class AddExerciseViewState(
    val exerciseName: String?,
    val isLoading: Boolean = false,
    val clearButtonText: String = "Clear",
    val repText: String = "",
    val weightText: String = "",
    val workoutSets: LiveData<List<WorkoutSet>>,
    val commentText: String = "",
    val secondsLeftLiveData: LiveData<String>,
    val secondsLeftString : String = "",
    val timerButtonText: String = "Start",
    val history: List<WorkoutExercise> = ArrayList()
)