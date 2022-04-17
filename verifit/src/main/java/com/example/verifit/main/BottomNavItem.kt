package com.example.verifit.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.verifit.R

sealed class BottomNavItem(var title:String, var icon:ImageVector, var screen_route:String){

    object Home : BottomNavItem("Main", Icons.Filled.AddCircle,"main")
    object Exercises: BottomNavItem("Exercises",Icons.Filled.FitnessCenter,"my_network")
    object Diary: BottomNavItem("Diary",Icons.Filled.Ballot,"diary")
    object Charts: BottomNavItem("Charts",Icons.Filled.Ballot,"charts")

}