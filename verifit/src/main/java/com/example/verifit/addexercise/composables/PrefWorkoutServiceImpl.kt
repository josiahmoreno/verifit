package com.example.verifit.addexercise.composables

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.MainActivity
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefWorkoutServiceImpl(val applicationContext: Context) :
    WorkoutService {

    lateinit var data : MutableLiveData<List<WorkoutSet>>

    override fun addSet(position: Int, workoutSet: WorkoutSet) {
        MainActivity.Workout_Days[position].addSet(workoutSet)
        saveToSharedPreferences()
        data.postValue(ArrayList(fetchSets(workoutSet.exercise)))
    }

    override fun addWorkoutDay(workoutDay: WorkoutDay, exerciseName: String?) {
        MainActivity.Workout_Days.add(workoutDay)
        saveToSharedPreferences()
        data.postValue(ArrayList(fetchSets(exerciseName)))
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
        data.value = ArrayList(fetchSets(toBeRemovedSet.exercise))
    }



    private fun fetchSets(exerciseName: String?): ArrayList<WorkoutSet> {
        val Todays_Exercise_Sets = ArrayList<WorkoutSet>()
        // Find Sets for a specific date and exercise
        for (i in MainActivity.Workout_Days.indices) {
            // If date matches
            if (MainActivity.Workout_Days[i].date == MainActivity.date_selected) {
                for (j in MainActivity.Workout_Days[i].sets.indices) {
                    // If exercise matches
                    if (exerciseName == MainActivity.Workout_Days[i].sets[j].exercise) {
                        Todays_Exercise_Sets.add(MainActivity.Workout_Days[i].sets[j])
                    }
                }
            }
        }
        return Todays_Exercise_Sets
    }
    override fun fetchWorkSets(exerciseName: String?): LiveData<List<WorkoutSet>> {
        val Todays_Exercise_Sets = fetchSets(exerciseName = exerciseName)
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

    override fun getExercise(exerciseName: String?): WorkoutExercise? {
        val exercise_position =
            MainActivity.getExercisePosition(MainActivity.date_selected, exerciseName)
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

    override fun calculateMaxWeight(exerciseName: String?): Pair<String, String> {
        var max_weight = 0.0
        var max_reps = 0
        var max_exercise_volume = 0.0

        // Find Max Weight and Reps for a specific exercise
        for (i in MainActivity.Workout_Days.indices) {
            for (j in MainActivity.Workout_Days[i].sets.indices) {
                if (MainActivity.Workout_Days[i].sets[j].volume > max_exercise_volume && MainActivity.Workout_Days[i].sets[j].exercise == exerciseName) {
                    max_exercise_volume = MainActivity.Workout_Days[i].sets[j].volume
                    max_reps = Math.round(MainActivity.Workout_Days[i].sets[j].reps)
                        .toInt()
                    max_weight = MainActivity.Workout_Days[i].sets[j].weight
                }
            }
        }

        // If never performed the exercise leave Edit Texts blank
        return if (max_reps == 0 || max_weight == 0.0) {
            Pair("", "")
        } else {
            Pair(max_reps.toString(), max_weight.toString())
        }
    }

    override fun fetchWorkoutDays(): List<WorkoutDay> {

        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("workouts", null)
        val type = object : TypeToken<ArrayList<WorkoutDay?>?>() {}.type
        var workoutDays : java.util.ArrayList<WorkoutDay>? = gson.fromJson(json, type)

        // If there are no previously saved entries make a new object
        if (workoutDays == null) {
            workoutDays = ArrayList()
        }

        return workoutDays
    }

    private fun saveToSharedPreferences(){
        // Sort Before Saving
        MainActivity.sortWorkoutDaysDate()
        // Actually Save Changes in shared preferences
        MainActivity.saveWorkoutData(applicationContext)
    }

}
