package com.example.verifit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.verifit.*
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutServiceImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")


class PrefDataStoreWorkoutService(val context: Context,dateSelectStore: DateSelectStore,
                                  knownExerciseService: KnownExerciseService): WorkoutServiceImpl(dateSelectStore, knownExerciseService){
    val dataStore = context.dataStore
    override fun initialFetchWorkoutDaysFromPreferences(): ArrayList<WorkoutDay> = runBlocking {

           val gson = Gson()

           val EXAMPLE_COUNTER = stringPreferencesKey("workouts")
           val exampleCounterFlow: Flow<ArrayList<WorkoutDay>> = context.dataStore.data
               .map { preferences ->
                   val json = preferences[EXAMPLE_COUNTER]
                   val type = object : TypeToken<ArrayList<WorkoutDay?>?>() {}.type
                   // No type safety.
                   gson.fromJson(json, type) ?: ArrayList<WorkoutDay>()
               }
           exampleCounterFlow.first()
    }

    override  fun saveToSharedPreferences() = runBlocking{
        val EXAMPLE_COUNTER = stringPreferencesKey("workouts")
        dataStore.edit { settings ->
            val gson = Gson()
            val json = gson.toJson(workoutDays)
            settings[EXAMPLE_COUNTER] = json
            }
        Unit
    }

}

class PrefKnownExerciseServiceDataStoreImpl(val context: Context) : DefaultKnownExercise(emptyList()) {
    override val _knownExercises: MutableList<Exercise> = ArrayList<Exercise>(fetchKnownExercises())

    val dataStore = context.dataStore
    override fun fetchKnownExercises(): List<Exercise> = runBlocking {
        val gson = Gson()

        val key = stringPreferencesKey("known_exercises")
        val exampleCounterFlow: Flow<ArrayList<Exercise>> = context.dataStore.data
            .map { preferences ->
                val json = preferences[key]
                val type = object : TypeToken<ArrayList<Exercise?>?>() {}.type
                // No type safety.
                gson.fromJson(json, type) ?: ArrayList<Exercise>()
            }
        val list = exampleCounterFlow.first()
        if(list.isEmpty()){
            defaultKnownExercies(list)
        }
        list
    }


    override fun saveKnownExerciseDataToPreferences() = runBlocking {

        val EXAMPLE_COUNTER = stringPreferencesKey("known_exercises")
        dataStore.edit { settings ->
            val gson = Gson()
            val json = gson.toJson(knownExercises)
            settings[EXAMPLE_COUNTER] = json
        }
        Unit
    }


}