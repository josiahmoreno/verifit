package com.example.verifit.di

import android.content.Context
import androidx.navigation.NavHostController
import com.example.verifit.ColorGetter
import com.example.verifit.KnownExerciseService
import com.example.verifit.addexercise.history.FetchHistoryUseCase
import com.example.verifit.common.*
import com.example.verifit.customexercise.SaveNewExerciseUseCase
import com.example.verifit.exercises.FetchExercisesListUseCase
import com.example.verifit.main.FetchViewPagerDataUseCase
import com.example.verifit.settings.ToastMaker
import com.example.verifit.workoutservice.WorkoutService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class) // Scope our dependencies
@Module
class UseCaseModule {



    // To be read as — When someone asks for DataRepository, create a DataRepoImpl and return it.
    @Provides
    fun getFetchHistoryUseCase(workoutService: WorkoutService): FetchHistoryUseCase {
        return FetchHistoryUseCase(workoutService)
    }

    @Provides
    fun getFetchGraphDialogDataUseCase(workoutService: WorkoutService): FetchGraphDialogDataUseCase {
        return FetchGraphDialogDataUseCaseImpl(workoutService)
    }


    @Provides
    fun getFetchViewPagerDataUseCase(workoutService: WorkoutService, colorGetter: ColorGetter): FetchViewPagerDataUseCase {
        return FetchViewPagerDataUseCase(workoutService, colorGetter = colorGetter)
    }

    @Provides
    fun getFetchExercisesListUseCase(knownExerciseService: KnownExerciseService): FetchExercisesListUseCase {
        return FetchExercisesListUseCase(knownExerciseService)
    }

    @Provides
    fun getNavigateToAddExerciseUseCase(navHostController: NavHostController): NavigateToAddExerciseUseCase {
        return NavigateToAddExerciseUseCaseImpl(navHostController)
    }

    @Provides
    fun getNavigateToExercisesListUseCase(navHostController: NavHostController): NavigateToExercisesListUseCase {
        return NavigateToExercisesListUseCaseImpl(navHostController)
    }

    @Provides
    fun getNavigateToHistoryDialogUseCase(navHostController: NavHostController): NavigateToHistoryDialogUseCase {
        return NavigateToHistoryDialogUseCaseImpl(navHostController)
    }

    @Provides
    fun getNavigateToGraphDialogUseCase(navHostController: NavHostController): NavigateToGraphDialogUseCase {
        return NavigateToGraphDialogUseCaseImpl(navHostController)
    }

    @Provides
    fun getNavigateToTimerUseCase(navHostController: NavHostController): NavigateToTimerUseCase {
        return NavigateToTimerUseCaseImpl(navHostController)
    }
    @Provides
    fun getNavigateToCommentUseCase(navHostController: NavHostController): NavigateToCommentUseCase {
        return NavigateToCommentUseCaseImpl(navHostController)
    }
    @Provides
    fun getNavigateToDeleteSetDialogUseCase(navHostController: NavHostController): NavigateToDeleteSetDialogUseCase {
        return NavigateToDeleteSetDialogUseCaseImpl(navHostController)
    }

    @Provides
    fun getListenToCommentResultsUseCase(navHostController: NavHostController): ListenToCommentResultsUseCase {
        return ListenToCommentResultsUseCaseImpl(navHostController)
    }


    @Provides
    fun getGoToNewCustomExerciseCase(navHostController: NavHostController): GoToNewCustomExerciseCase {
        return NavigateToNewCustomExerciseCaseImpl(navHostController)
    }

    @Provides
    fun getSaveNewExerciseUseCase(knownExerciseService: KnownExerciseService, @ApplicationContext context: Context, toastMaker: ToastMaker): SaveNewExerciseUseCase {
        return SaveNewExerciseUseCase(knownExerciseService = knownExerciseService,
        context = context,
                toastMaker = toastMaker
                )
    }

    //show
    @Provides
    fun getShowExerciseStatsUseCase(): ShowExerciseStatsUseCase {
        return ShowExerciseStatsUseCase(
        )
    }

    @Provides
    fun getShowSetStatsUseCase(): ShowSetStatsUseCase {
        return ShowSetStatsUseCase(
        )
    }


}