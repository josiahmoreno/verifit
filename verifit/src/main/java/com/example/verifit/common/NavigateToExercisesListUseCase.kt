package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateToExercisesListUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToExercisesListUseCase {
    override operator fun invoke(date:String)  {navHostController.navigate("list?date=${date}")}

}

class MockNavigateToExercisesListUseCase: NavigateToExercisesListUseCase {



}

interface NavigateToExercisesListUseCase {
     operator fun invoke(date:String){

     }
//    operator fun invoke() {
//
//    }
}