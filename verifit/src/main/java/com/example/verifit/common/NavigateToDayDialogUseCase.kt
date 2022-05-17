package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToDayDialogUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToDayDialogUseCase {
    override operator fun invoke(date:String) { navHostController.navigate("day_list_dialog/${date}")}
}

class NoOpNavigateToDayDialogUseCase: NavigateToDayDialogUseCase

interface NavigateToDayDialogUseCase {
     operator fun invoke(date:String){

     }
}