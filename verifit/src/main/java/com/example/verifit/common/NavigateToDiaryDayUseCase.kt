package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToDiaryDayUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToDiaryDayUseCase {
    override operator fun invoke(date:String) {navHostController.navigate("diary_day_stats/${date}")}
}

class MockNavigateToDiaryDayUseCase(): NavigateToDiaryDayUseCase {
    override fun invoke(date:String) {

    }

}

interface NavigateToDiaryDayUseCase {
    operator fun invoke(date:String)
}