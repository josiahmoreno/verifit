package com.example.verifit.di

import androidx.navigation.NavHostController
import com.example.verifit.common.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class navigationmoduel {
    @Binds
    abstract fun getNavigateToAddExerciseUseCase(navHostController: NavigateToAddExerciseUseCaseImpl): NavigateToAddExerciseUseCase

    @Binds
    abstract fun getNavigateToExercisesListUseCase(navHostController: NavigateToExercisesListUseCaseImpl): NavigateToExercisesListUseCase


    @Binds
    abstract fun getNavigateToHistoryDialogUseCase(navHostController: NavigateToHistoryDialogUseCaseImpl): NavigateToHistoryDialogUseCase

    @Binds
    abstract fun getNavigateToGraphDialogUseCase(navHostController: NavigateToGraphDialogUseCaseImpl): NavigateToGraphDialogUseCase

    @Binds
    abstract fun getNavigateToTimerUseCase(navHostController: NavigateToTimerUseCaseImpl): NavigateToTimerUseCase
    @Binds
    abstract fun getNavigateToCommentUseCase(navHostController: NavigateToCommentUseCaseImpl): NavigateToCommentUseCase
    @Binds
    abstract fun navigateToDayActivityUseCaseImpl(navHostController: NavigateToDayActivityUseCaseImpl): NavigateToDayActivityUseCase
    @Binds
    abstract fun navigateToDayDialogUseCaseImpl(navHostController: NavigateToDayDialogUseCaseImpl): NavigateToDayDialogUseCase
    @Binds
    abstract fun navigateToDiaryDayUseCase(navHostController: NavigateToDiaryDayUseCaseImpl): NavigateToDiaryDayUseCase
    @Binds
    abstract fun navigateToExerciseEntryStatsUseCase(navHostController: NavigateToExerciseEntryStatsUseCaseImpl): NavigateToExerciseEntryStatsUseCase
    @Binds
    abstract fun navigateToDiaryListUseCase(navHostController: NavigateToDiaryListUseCaseImpl): NavigateToDiaryListUseCase

    @Binds
    abstract fun navigateToViewPagerUseCase(navHostController: NavigateToViewPagerUseCaseImpl): NavigateToViewPagerUseCase

    @Binds
    abstract fun getNavigateToDeleteSetDialogUseCase(navHostController: NavigateToDeleteSetDialogUseCaseImpl): NavigateToDeleteSetDialogUseCase

    @Binds
    abstract fun navigateToCalendarUseCase(navHostController: NavigateToCalendarUseCaseImpl): NavigateToCalendarUseCase

    @Binds
    abstract  fun navigateToSettingsUseCase(navHostController: NavigateToSettingsUseCaseImpl): NavigateToSettingsUseCase




    @Binds
    abstract fun getGoToNewCustomExerciseCase(navHostController: NavigateToNewCustomExerciseCaseImpl): GoToNewCustomExerciseCase


}

@InstallIn(ViewModelComponent::class)
@Module
abstract class  ViewModelModule{
    @Binds
    abstract fun getListenToCommentResultsUseCase (navHostController: ListenToCommentResultsUseCaseImpl): ListenToCommentResultsUseCase
}