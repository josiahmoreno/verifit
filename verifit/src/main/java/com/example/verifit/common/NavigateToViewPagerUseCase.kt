package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToViewPagerUseCaseImpl(val navHostController: NavHostController): NavigateToViewPagerUseCase {
    override operator fun invoke(date:String) = navHostController.navigate("view_pager?date${date}")
}

class NoOpNavigateToViewPagerUseCase : NavigateToViewPagerUseCase

interface NavigateToViewPagerUseCase {
    operator fun invoke(date:String){

    }
}