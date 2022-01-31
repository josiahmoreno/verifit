package com.example.verifit

import androidx.lifecycle.LiveData

interface WorkoutService {
    fun addSet(workoutSet: WorkoutSet)
    fun addSet(position: Int, workoutSet: WorkoutSet)
    fun addWorkoutDay(workoutDay: WorkoutDay)
    fun removeSet(toBeRemovedSet: WorkoutSet)
    fun fetchWorkSets() : LiveData<List<WorkoutSet>>

}
