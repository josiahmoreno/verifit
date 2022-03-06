package com.example.verifit

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface KnownExerciseService {
    var knownExercises : List<Exercise>
    fun doesExerciseExist(exercise_name: String): Boolean {
        for (i in knownExercises.indices) {
            if (MainActivity.KnownExercises[i].name == exercise_name) {
                return true
            }
        }
        return false
    }

    fun saveKnownExerciseData(new_exercise: Exercise)
}

class PrefKnownExerciseServiceImpl(private val applicationContext: Context) : KnownExerciseService{
    val _knownExercises = mutableListOf<Exercise>()
    override var knownExercises : List<Exercise> = _knownExercises // Initialized with hardcoded exercises
    override fun saveKnownExerciseData(newExercise: Exercise) {
        _knownExercises.add(newExercise)
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(knownExercises)
        editor.putString("known_exercises", json)
        editor.apply()
    }

    init {
        loadKnownExercisesData()
    }


    fun loadKnownExercisesData(){
        if (knownExercises.isEmpty()) {
            val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("known_exercises", null)
            val type = object : TypeToken<ArrayList<Exercise>?>() {}.type
            val KnownExercises2 : java.util.ArrayList<Exercise>? = gson.fromJson(json, type)

            //var workoutDays : java.util.ArrayList<WorkoutDay>? = gson.fromJson(json, type)
            KnownExercises2?.let{
                _knownExercises.addAll(KnownExercises2)
            }


            // If there are no previously saved entries make a new object
            if (knownExercises == null || knownExercises.isEmpty()) {
                knownExercises = ArrayList()
                initKnownExercises()
            }
        }
    }

    fun initKnownExercises() {
        _knownExercises.clear()
        // Some hardcoded Exercises
        _knownExercises.add(Exercise("Flat Barbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Incline Barbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Decline Barbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Flat Dumbbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Incline Dumbbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Decline Dumbbell Bench Press", "Chest"))
        _knownExercises.add(Exercise("Chin Up", "Back"))
        _knownExercises.add(Exercise("Seated Dumbbell Press", "Shoulders"))
        _knownExercises.add(Exercise("Ring Dip", "Chest"))
        _knownExercises.add(Exercise("Lateral Cable Raise", "Shoulders"))
        _knownExercises.add(Exercise("Lateral Dumbbell Raise", "Shoulders"))
        _knownExercises.add(Exercise("Barbell Curl", "Biceps"))
        _knownExercises.add(Exercise("Tricep Extension", "Triceps"))
        _knownExercises.add(Exercise("Squat", "Legs"))
        _knownExercises.add(Exercise("Leg Extension", "Legs"))
        _knownExercises.add(Exercise("Hammstring Leg Curl", "Legs"))
        _knownExercises.add(Exercise("Deadlift", "Back"))
        _knownExercises.add(Exercise("Sumo Deadlift", "Back"))
        _knownExercises.add(Exercise("Seated Machine Chest Press", "Chest"))
        _knownExercises.add(Exercise("Seated Machine Shoulder Press", "Shoulders"))
        _knownExercises.add(Exercise("Seated Calf Raise", "Legs"))
        _knownExercises.add(Exercise("Donkey Calf Raise", "Legs"))
        _knownExercises.add(Exercise("Standing Calf Raise", "Legs"))
        _knownExercises.add(Exercise("Seated Machine Curl", "Biceps"))
        _knownExercises.add(Exercise("Lat Pulldown", "Back"))
        _knownExercises.add(Exercise("Pull Up", "Back"))
        _knownExercises.add(Exercise("Push Up", "Chest"))
        _knownExercises.add(Exercise("Leg Press", "Legs"))
        _knownExercises.add(Exercise("Push Press", "Shoulders"))
        _knownExercises.add(Exercise("Dumbbell Curl", "Biceps"))
        _knownExercises.add(Exercise("Decline Hammer Strength Chest Press", "Chest"))
        _knownExercises.add(Exercise("Leg Extension Machine", "Legs"))
        _knownExercises.add(Exercise("Seated Calf Raise Machine", "Legs"))
        _knownExercises.add(Exercise("Lying Triceps Extension", "Triceps"))
        _knownExercises.add(Exercise("Cable Curl", "Biceps"))
        _knownExercises.add(Exercise("Hammer Strength Shoulder Press", "Shoulders"))
    }

}