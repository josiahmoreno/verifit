package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToDayActivityUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToDayActivityUseCase {
    override operator fun invoke(date:String) { navHostController.navigate("day_list/${date}")}
}

class NoOpNavigateToDayActivityUseCase: NavigateToDayActivityUseCase

interface NavigateToDayActivityUseCase {
     operator fun invoke(date:String){

     }
}