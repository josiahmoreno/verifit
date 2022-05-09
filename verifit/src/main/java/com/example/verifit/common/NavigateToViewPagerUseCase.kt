package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavHostController

class NavigateToViewPagerUseCaseImpl(val navHostController: NavHostController): NavigateToViewPagerUseCase {
    override operator fun invoke(date:String)  {
        Log.d("ViewPagerViewModel.NavigateToViewPagerUseCaseImpl","date = $date")
        navHostController.navigate("view_pager?date=${date}")
    }
}

class NoOpNavigateToViewPagerUseCase : NavigateToViewPagerUseCase

interface NavigateToViewPagerUseCase {
    operator fun invoke(date:String){

    }
}