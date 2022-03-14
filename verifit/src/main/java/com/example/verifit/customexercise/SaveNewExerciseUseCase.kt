package com.example.verifit.customexercise

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.Exercise
import com.example.verifit.ExercisesActivity
import com.example.verifit.KnownExerciseService
import com.example.verifit.exercises.Compose_ExercisesActivity
import com.example.verifit.exercises.ExercisesListDataResult

class SaveNewExerciseUseCase(val knownExerciseService: KnownExerciseService, val context: Context) {
    operator fun invoke(exerciseName: String, selectedCategory: String) = saveNewExercise(exerciseName, selectedCategory)

    @OptIn(ExperimentalComposeUiApi::class)
    private fun saveNewExercise(exerciseName: String, selectedCategory: String) {
        if(exerciseName.isEmpty()){
            Toast.makeText(context, "Exercise Name Empty.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!knownExerciseService.doesExerciseExist(exerciseName)) {
            val new_exercise = Exercise(exerciseName, selectedCategory)
            knownExerciseService.saveKnownExerciseData(new_exercise)
            Toast.makeText(context, "Exercise Saved", Toast.LENGTH_SHORT).show()
            val `in` = Intent(context, Compose_ExercisesActivity::class.java)
            context.startActivity(`in`)
        } else {
            Toast.makeText(context, "Exercise Already Exists", Toast.LENGTH_SHORT).show()
        }
    }
}
