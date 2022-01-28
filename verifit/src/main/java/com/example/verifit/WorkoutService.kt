package com.example.verifit

interface WorkoutService {
    fun addSet(workoutSet: WorkoutSet)
    fun addSet(position: Int, workoutSet: WorkoutSet)
    fun addWorkoutDay(workoutDay: WorkoutDay)

}
