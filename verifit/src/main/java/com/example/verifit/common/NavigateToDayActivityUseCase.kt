package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.main.BottomNavItem

class NavigateToDayActivityUseCaseImpl(val navHostController: NavHostController): NavigateToDayActivityUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("day/${date}")
}

class MockNavigateToDayActivityUseCase(): NavigateToDayActivityUseCase {
    override fun invoke(date:String) {

    }

}

interface NavigateToDayActivityUseCase {
     operator fun invoke(date:String)
}