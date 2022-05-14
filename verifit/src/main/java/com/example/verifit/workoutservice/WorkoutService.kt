package com.example.verifit.workoutservice

import androidx.lifecycle.LiveData
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.diary.DiaryEntry
import kotlinx.coroutines.flow.StateFlow

interface WorkoutService {
    fun addSet(position: Int, workoutSet: WorkoutSet)
    fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?)
    fun removeSet(toBeRemovedSet: WorkoutSet)
    fun fetchWorkSets(exerciseName: String?, date: String): LiveData<List<WorkoutSet>>
    fun fetchWorkoutExercise(exerciseName: String?, date: String): LiveData<WorkoutExercise>
    fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String)
    fun updateWorkoutSet( dateSelected: String?,
                          workoutSet: WorkoutSet
    )
    fun getExercise(exerciseName: String?): WorkoutExercise?
    fun getExercisesWithName(exerciseName : String): List<WorkoutExercise>
    fun calculateMaxWeight(exerciseName: String?):  Pair<String,String>
    fun fetchWorkoutDays() : List<WorkoutDay>
    fun saveWorkoutData()
    fun clearWorkoutData()
    fun saveToSharedPreferences()
    fun fetchDayPosition(dateSelected: String?): Int
    fun saveData(mutableListOf: List<WorkoutDay>)
    fun fetchDay(date: String): WorkoutDay
    fun fetchDayLive(date: String): LiveData<WorkoutDay>
    fun fetchSet(identifier: String): WorkoutSet
    fun fetchWorkoutDaysLive(): LiveData<List<WorkoutDay>>
}
