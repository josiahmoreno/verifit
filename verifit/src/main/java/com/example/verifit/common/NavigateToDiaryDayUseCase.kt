package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToDiaryDayUseCaseImpl(val navHostController: NavHostController): NavigateToDiaryDayUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("diary_day_stats/${date}")
}

class MockNavigateToDiaryDayUseCase(): NavigateToDiaryDayUseCase {
    override fun invoke(date:String) {

    }

}

interface NavigateToDiaryDayUseCase {
    operator fun invoke(date:String)
}