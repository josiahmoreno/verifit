package com.example.verifit.common

import androidx.navigation.NavHostController
import com.example.verifit.WorkoutDay
import com.example.verifit.workoutservice.WorkoutService

class NavigateToCommentUseCaseImpl(val workoutService: WorkoutService,val navigatorController: NavHostController): NavigateToCommentUseCase {
    override operator fun invoke(
        date: kotlin.String,
        exerciseName: kotlin.String,
        comment: String
    ) {

            navigatorController.navigate("comment/${exerciseName}/${date}?comment=${comment}"){

            }
    }

}

class NoOpNavigateToCommentUseCase(): NavigateToCommentUseCase{
    override fun invoke(
        date: kotlin.String,
        exerciseName: kotlin.String,
        comment: String
    ) {
        //TODO("Not yet implemented")
    }

}

interface NavigateToCommentUseCase{
    operator fun invoke(
        date: kotlin.String,
        exerciseName: kotlin.String,
        comment: String
    )
}