package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject

class NavigateToDiaryListUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToDiaryListUseCase {
    override operator fun invoke(date: String?)
        { navHostController.navigate("diary_list?${date}") }
}

class NoOpNavigateToDiaryListUseCase(): NavigateToDiaryListUseCase




interface NavigateToDiaryListUseCase {
    operator fun invoke(date:String?){

    }
}