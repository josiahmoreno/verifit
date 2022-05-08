package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToDayActivityUseCaseImpl(val navHostController: NavHostController): NavigateToDayActivityUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("day_list/${date}")
}

class NoOpNavigateToDayActivityUseCase: NavigateToDayActivityUseCase

interface NavigateToDayActivityUseCase {
     operator fun invoke(date:String){

     }
}