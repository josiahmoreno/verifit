package com.example.verifit.common

import android.util.Log
import androidx.navigation.NavHostController
import com.example.verifit.navigationhost.AuroraNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateToViewPagerUseCaseImpl @Inject constructor(val navHostController: AuroraNavigator): NavigateToViewPagerUseCase {
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