package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToDayDialogUseCaseImpl(val navHostController: NavHostController): NavigateToDayDialogUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("day_list_dialog/${date}")
}

class NoOpNavigateToDayDialogUseCase: NavigateToDayDialogUseCase

interface NavigateToDayDialogUseCase {
     operator fun invoke(date:String){

     }
}