package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToExercisesListUseCaseImpl(val navHostController: NavHostController): NavigateToExercisesListUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("list?date=${date}"){
        val id = navHostController.graph.findStartDestination().id
        val route = navHostController.graph.startDestinationRoute
        val prev1 =  navHostController.previousBackStackEntry?.destination?.parent?.route
        val prev2 =  navHostController.previousBackStackEntry?.destination?.route
        val prev3 =  navHostController.currentBackStackEntry?.destination?.route
        Log.d("navhost","NNNNNNNNNNNNNavigateToExercisesListUseCaseImpl $id, route = $route,$prev1,$prev2, prev3 $prev3" )

        popUpTo("$prev3" ) {
           // saveState = true

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