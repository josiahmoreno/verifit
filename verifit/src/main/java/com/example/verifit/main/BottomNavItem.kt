package com.example.verifit.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title:String, var icon:ImageVector, var screen_route:String){




     object Home : BottomNavItem("Main", Icons.Filled.AddCircle,"main")


    //object Exercises: BottomNavItem("Exercises",Icons.Filled.FitnessCenter,"exercises")
    object  Exercises: BottomNavItem("Exercises", Icons.Filled.FitnessCenter,"exercises")

    object Diary: BottomNavItem("Diary",Icons.Filled.Ballot,"diary")
    object Charts: BottomNavItem("Charts",Icons.Filled.PieChart,"charts")
    object Me: BottomNavItem("Me",Icons.Filled.Person,"me")

    companion object {
        fun fromRoute(route: String?): BottomNavItem =
            when (route?.substringBefore("/")) {
                Home.title -> Home
                Exercises.title -> Exercises
                Diary.title -> Diary
                Charts.title -> Charts
                Me.title -> Me
                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }

        fun isARoot(route: String?): Boolean = when(route) {
                Home.title -> true
                Exercises.title -> true
                Diary.title -> true
                Charts.title -> true
                Me.title -> true
                null -> true
                else -> false
        }
    }
}