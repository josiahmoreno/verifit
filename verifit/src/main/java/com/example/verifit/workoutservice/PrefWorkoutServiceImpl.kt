package com.example.verifit.workoutservice

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.verifit.*
import com.example.verifit.singleton.DateSelectStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class PrefWorkoutServiceImpl(val applicationContext: Context,
                             dateSelectStore: DateSelectStore,
                             knownExerciseService: KnownExerciseService) :
    WorkoutServiceImpl(dateSelectStore = dateSelectStore,
            knownExerciseService = knownExerciseService) {

    init {
        //every subclass of WorkoutServiceImpl must calculate
        //calculatePersonalRecords(knownExerciseService.knownExercises)
    }

    override fun saveWorkoutData() {
        val sharedPreferences = applicationContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(workoutDays)
        editor.putString("workouts", json)
        editor.apply()
    }

    override fun initialFetchWorkoutDaysFromPreferences(): ArrayList<WorkoutDay> {
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


}
