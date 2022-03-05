package com.example.verifit.addexercise.composables

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet

class FakeWorkoutService: WorkoutService {
    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?) {
        TODO("Not yet implemented")
    }

    override fun removeSet(toBeRemovedSet: WorkoutSet) {
        TODO("Not yet implemented")
    }

    override fun fetchWorkSets(excerciseName: String?): LiveData<List<WorkoutSet>> {
        return MutableLiveData(
            listOf(WorkoutSet("", "", "", 11.0, 11.0),
                WorkoutSet("", "", "", 200.0, 8.0)
            ),
        )
    }

    override fun updateComment(dateSelected: String?, exerciseKey: String?, exerciseComment: String) {
        TODO("Not yet implemented")
    }

    override fun getExercise(exerciseName: String?): WorkoutExercise? {
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override fun calculateMaxWeight(exerciseName: String?): Pair<String, String> {
        return Pair("1.0","1.0")
    }

    override fun fetchWorkoutDays(): List<WorkoutDay> {
        TODO("Not yet implemented")
    }

}