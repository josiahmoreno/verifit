package com.example.verifit.common

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToExercisesListUseCaseImpl(val navHostController: NavHostController): NavigateToExercisesListUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("list?date=${date}"){
        val id = navHostController.graph.findStartDestination().id
        popUpTo(id ) {
            saveState = true
        }
    }

//    override operator fun invoke() = navHostController.navigate("list?date=${null}"){
//        val id = navHostController.graph.findStartDestination().id
//        popUpTo(id ) {
//            saveState = true
//        }
//    }

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