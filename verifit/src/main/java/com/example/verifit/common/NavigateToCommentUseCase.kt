package com.example.verifit.common

import androidx.navigation.NavHostController

class NavigateToCommentUseCaseImpl(val navigatorController: NavHostController): NavigateToCommentUseCase {
    override operator fun invoke(exerciseName: String) {
            navigatorController.navigate("comment/${exerciseName}")
    }

}

class NoOpNavigateToCommentUseCase(): NavigateToCommentUseCase{
    override fun invoke(exerciseName: String) {
        //TODO("Not yet implemented")
    }

}

interface NavigateToCommentUseCase{
    operator fun invoke(exerciseName: String)
}