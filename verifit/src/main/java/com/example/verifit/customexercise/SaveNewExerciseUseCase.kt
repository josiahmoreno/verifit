package com.example.verifit.customexercise

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.Exercise
import com.example.verifit.ExercisesActivity
import com.example.verifit.KnownExerciseService
import com.example.verifit.common.NavigateToExercisesListUseCase
import com.example.verifit.exercises.Compose_ExercisesActivity
import com.example.verifit.exercises.ExercisesListDataResult
import com.example.verifit.settings.ToastMaker

class SaveNewExerciseUseCase(val knownExerciseService: KnownExerciseService, val context: Context, val toastMaker: ToastMaker, val navigateToExercisesList: NavigateToExercisesListUseCase) {
    operator fun invoke(exerciseName: String, selectedCategory: String) = saveNewExercise(exerciseName, selectedCategory)

    @OptIn(ExperimentalComposeUiApi::class)
    private fun saveNewExercise(exerciseName: String, selectedCategory: String) {
        if(exerciseName.isEmpty()){
            toastMaker.makeText("Exercise Name Empty.")
            return
        }
        if (!knownExerciseService.doesExerciseExist(exerciseName)) {
            val new_exercise = Exercise(exerciseName, selectedCategory)
            knownExerciseService.saveKnownExerciseData(new_exercise)
            toastMaker.makeText("Exercise Saved")

            navigateToExercisesList()
        } else {
            toastMaker.makeText("Exercise Already Exists")
        }
    }
}
