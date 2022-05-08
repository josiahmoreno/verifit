package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToDiaryListUseCaseImpl(val navHostController: NavHostController): NavigateToDiaryListUseCase {
    override operator fun invoke(date:String?) = navHostController.navigate("diary_list?${date}")
}

class NoOpNavigateToDiaryListUseCase(): NavigateToDiaryListUseCase




interface NavigateToDiaryListUseCase {
    operator fun invoke(date:String?){

    }
}