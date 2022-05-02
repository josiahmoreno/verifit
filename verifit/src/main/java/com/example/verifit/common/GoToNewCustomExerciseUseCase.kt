package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class GoToNewCustomExerciseCase ( val goToNewCustomExercise: (() -> Unit), val navHostController: NavHostController? = null){
    operator fun invoke() = navHostController?.navigate("new_exercise")
}