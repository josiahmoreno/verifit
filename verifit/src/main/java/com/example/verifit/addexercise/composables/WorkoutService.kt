package com.example.verifit.addexercise.composables

import androidx.lifecycle.LiveData
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet

interface WorkoutService {
    fun addSet(position: Int, workoutSet: WorkoutSet)
    fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?)
    fun removeSet(toBeRemovedSet: WorkoutSet)
    fun fetchWorkSets(exerciseName: String?): LiveData<List<WorkoutSet>>
    fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String)
    fun getExercise(exerciseName: String?): WorkoutExercise?
    fun getExercisesWithName(exerciseName : String): List<WorkoutExercise>
    fun calculateMaxWeight(exerciseName: String?):  Pair<String,String>
    fun fetchWorkoutDays() : List<WorkoutDay>
}
