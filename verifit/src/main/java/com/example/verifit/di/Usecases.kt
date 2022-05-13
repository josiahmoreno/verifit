package com.example.verifit.di

import android.content.Context
import androidx.navigation.NavHostController
import com.example.verifit.ColorGetter
import com.example.verifit.KnownExerciseService
import com.example.verifit.addexercise.deleteset.DeleteSetUseCase
import com.example.verifit.addexercise.history.FetchHistoryUseCase
import com.example.verifit.charts.FetchChartsDataUseCase
import com.example.verifit.charts.FetchChartsDataUseCaseImpl
import com.example.verifit.common.*
import com.example.verifit.customexercise.SaveNewExerciseUseCase
import com.example.verifit.diary.*
import com.example.verifit.exercises.FetchExercisesListUseCase
import com.example.verifit.main.FetchViewPagerDataUseCase
import com.example.verifit.settings.ToastMaker
import com.example.verifit.singleday.FetchDaysWorkoutsUseCase
import com.example.verifit.singleday.FetchDaysWorkoutsUseCaseImpl
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
    fun fetchChartsDataUseCase(workoutService: WorkoutService): FetchChartsDataUseCase {
        return FetchChartsDataUseCaseImpl(workoutService)
    }

    @Provides
    fun fetchDaysWorkoutsUseCase(workoutService: WorkoutService, colorGetter: ColorGetter): FetchDaysWorkoutsUseCase {
        return FetchDaysWorkoutsUseCaseImpl(workoutService, colorGetter)
    }
    @Provides
    fun calculatedDiaryEntryUseCase(): CalculatedDiaryEntryUseCase {
        return CalculatedDiaryEntryUseCaseImpl()
    }

    @Provides
    fun calculatedExerciseEntryUseCase(workoutService: WorkoutService): CalculatedExerciseEntryUseCase {
        return CalculatedExerciseEntryUseCaseImpl(workoutService = workoutService)
    }

    @Provides
    fun deleteSetUseCase(workoutService: WorkoutService): DeleteSetUseCase {
        return DeleteSetUseCase(workoutService)
    }
    @Provides
    fun getFetchHistoryUseCase(workoutService: WorkoutService): FetchHistoryUseCase {
        return FetchHistoryUseCase(workoutService)
    }

    @Provides
    fun getFetchDiaryUseCase(workoutService: WorkoutService, knownExerciseService: KnownExerciseService): FetchDiaryUseCase {
        return FetchDiaryUseCaseImpl(workoutService,knownExerciseService)
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
    fun navigateToDayActivityUseCaseImpl(navHostController: NavHostController): NavigateToDayActivityUseCase {
        return NavigateToDayActivityUseCaseImpl(navHostController)
    }
    @Provides
    fun navigateToDiaryDayUseCase(navHostController: NavHostController): NavigateToDiaryDayUseCase {
        return NavigateToDiaryDayUseCaseImpl(navHostController)
    }
    @Provides
    fun navigateToExerciseEntryStatsUseCase(navHostController: NavHostController): NavigateToExerciseEntryStatsUseCase {
        return NavigateToExerciseEntryStatsUseCaseImpl(navHostController)
    }
    @Provides
    fun navigateToDiaryListUseCase(navHostController: NavHostController): NavigateToDiaryListUseCase {
        return NavigateToDiaryListUseCaseImpl(navHostController)
    }
    @Provides
    fun navigateToViewPagerUseCase(navHostController: NavHostController): NavigateToViewPagerUseCase {
        return NavigateToViewPagerUseCaseImpl(navHostController)
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