package com.example.verifit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.ArrayList

class PrefWorkoutServiceImpl(val exercise_name: String?, val applicationContext: Context) : WorkoutService {

    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        MainActivity.Workout_Days[position].addSet(workoutSet)
        saveToSharedPreferences()
        data.postValue(ArrayList(MainActivity.Workout_Days[position].sets))
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay) {
        MainActivity.Workout_Days.add(workoutDay)
        saveToSharedPreferences()
        data.postValue(ArrayList(workoutDay.sets))
    }

    override fun removeSet(toBeRemovedSet: WorkoutSet) {
        for (i in MainActivity.Workout_Days.indices) {
            if (MainActivity.Workout_Days[i].sets.contains(toBeRemovedSet)) {
                // If last set the delete the whole object
                if (MainActivity.Workout_Days[i].sets.size == 1) {
                    MainActivity.Workout_Days.remove(MainActivity.Workout_Days[i])
                } else {
                    MainActivity.Workout_Days[i].removeSet(toBeRemovedSet)
                    break
                }
            }
        }
        saveToSharedPreferences()
        data.value = ArrayList(fetch())
    }

    lateinit var data : MutableLiveData<List<WorkoutSet>>

    private fun fetch(): ArrayList<WorkoutSet> {
        val Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        // Find Sets for a specific date and exercise
        for (i in MainActivity.Workout_Days.indices) {
            // If date matches
            if (MainActivity.Workout_Days[i].date == MainActivity.date_selected) {
                for (j in MainActivity.Workout_Days[i].sets.indices) {
                    // If exercise matches
                    if (exercise_name == MainActivity.Workout_Days[i].sets[j].exercise) {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days[i].sets[j])
                    }
                }
            }
        }
        return Todays_Exercise_Sets
    }
    override fun fetchWorkSets(): LiveData<List<WorkoutSet>> {
        val Todays_Exercise_Sets = fetch()
        data = MutableLiveData(Todays_Exercise_Sets)
        return data
    }

    override fun updateComment(
            dateSelected: String?,
            exerciseKey: String?,
            exerciseComment: String
    ) {
        val exercise_position =
                MainActivity.getExercisePosition(MainActivity.date_selected, exerciseKey)
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
        } else {
            println("We can't comment, exercise doesn't exist")
            return
        }
        //TODO Replace with  storage
        // Get the date for today
        val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
        // Modify the data structure to add the comment
        MainActivity.Workout_Days[day_position].exercises[exercise_position].comment = exerciseComment
        saveToSharedPreferences()
    }

    override fun GetExercise(): WorkoutExercise? {
        val exercise_position =
                MainActivity.getExercisePosition(MainActivity.date_selected, exercise_name)
        // Exists, then show the comment
        if (exercise_position >= 0) {
            println("We can comment, exercise exists")
            val day_position = MainActivity.getDayPosition(MainActivity.date_selected)
            return MainActivity.Workout_Days[day_position].exercises[exercise_position]
        }
        return null
    }

    override fun getExercisesWithName(exerciseName: String): List<WorkoutExercise> {
        // Find all performed sessions of a specific exercise and add them to local data structure
        val All_Performed_Sessions = ArrayList<WorkoutExercise>()
        for (i in MainActivity.Workout_Days.indices.reversed()) {
            for (j in MainActivity.Workout_Days[i].exercises.indices) {
                if (MainActivity.Workout_Days[i].exercises[j].exercise == exerciseName) {
                    All_Performed_Sessions.add(MainActivity.Workout_Days[i].exercises[j])
                }
            }
        }
        return All_Performed_Sessions
    }

    override fun calculateMaxWeight(): Pair<String, String> {
        TODO("Not yet implemented")
    }

    private fun saveToSharedPreferences(){
        // Sort Before Saving
        MainActivity.sortWorkoutDaysDate()
        // Actually Save Changes in shared preferences
        MainActivity.saveWorkoutData(applicationContext)
    }

}
