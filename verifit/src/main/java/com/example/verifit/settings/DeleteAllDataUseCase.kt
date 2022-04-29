package com.example.verifit.settings

import android.content.Context
import android.content.Intent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.verifit.main.Compose_MainActivity
import com.example.verifit.workoutservice.WorkoutService

class DeleteAllDataUseCase(
    private val context: Context,
    private val workoutService: WorkoutService,
    private val toastMaker: ToastMaker,
) {
    var EXPORT_FILENAME = "verifit_backup"
    
    operator fun invoke() = createDocument()

    @OptIn(ExperimentalComposeUiApi::class)
    private fun createDocument(){
        workoutService.clearWorkoutData()
        toastMaker.makeText("Data Deleted")

        val intent = Intent(context, Compose_MainActivity::class.java)
        context.startActivity(intent)
    }





}
