package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToCommentUseCaseImpl(val navigatorController: NavHostController): NavigateToCommentUseCase {
    override operator fun invoke(date: String,exerciseName: String) {
            navigatorController.navigate("comment/${exerciseName}/${date}")
    }

}

class NoOpNavigateToCommentUseCase(): NavigateToCommentUseCase{
    override fun invoke(date: String,exerciseName: String) {
        //TODO("Not yet implemented")
    }

}

interface NavigateToCommentUseCase{
    operator fun invoke(date: String, exerciseName: String)
}