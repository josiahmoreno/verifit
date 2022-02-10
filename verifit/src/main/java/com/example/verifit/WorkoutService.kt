package com.example.verifit

import androidx.lifecycle.LiveData

interface WorkoutService {
    fun addSet(position: Int, workoutSet: WorkoutSet)
    fun addWorkoutDay(workoutDay: WorkoutDay)
    fun removeSet(toBeRemovedSet: WorkoutSet)
    fun fetchWorkSets() : LiveData<List<WorkoutSet>>
    fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String)
    fun GetExercise(): WorkoutExercise?
    fun getExercisesWithName(exerciseName : String): List<WorkoutExercise>
    fun calculateMaxWeight():  Pair<String,String>
}
