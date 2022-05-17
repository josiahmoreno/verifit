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
import com.example.verifit.common.SaveNewExerciseUseCase
import com.example.verifit.diary.*
import com.example.verifit.exercises.FetchExercisesListUseCase
import com.example.verifit.main.FetchViewPagerDataUseCase
import com.example.verifit.settings.*
import com.example.verifit.singleday.FetchDaysWorkoutsUseCase
import com.example.verifit.singleday.FetchDaysWorkoutsUseCaseImpl
import com.example.verifit.workoutservice.WorkoutService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(ViewModelComponent::class) // Scope our dependencies
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
    fun getSaveNewExerciseUseCase(knownExerciseService: KnownExerciseService, @ApplicationContext context: Context, toastMaker: ToastMaker): SaveNewExerciseUseCase {
        return SaveNewExerciseUseCase(knownExerciseService = knownExerciseService,
        context = context,
                toastMaker = toastMaker
                )
    }

    @Provides
    fun updateWorkoutSetUseCase(workoutService: WorkoutService): UpdateWorkoutSetUseCase {
        return UpdateWorkoutSetUseCaseImpl(workoutService)
    }

    @Provides
    fun importDataUseCase(@ApplicationContext context: Context,
                             workoutService: WorkoutService,
                             toastMaker: ToastMaker,
                             knownExerciseService: KnownExerciseService
//                          writePermissionChecker: WritePermissionChecker,
//                           externalStorageChecker: ExternalStorageChecker,
        //createDocumentLauncherWrapper: ExportDataUseCase.CreateDocumentLauncherWrapper
    ): ImportDataUseCase {

        return ImportDataUseCase(context, toastMaker, knownExerciseService, workoutService)
    }

    @Provides
    fun importCSVDataUseCase(@ApplicationContext context: Context,
                          workoutService: WorkoutService,
                         toastMaker: ToastMaker,
        knownExerciseService: KnownExerciseService
//                          writePermissionChecker: WritePermissionChecker,
//                           externalStorageChecker: ExternalStorageChecker,
                          //createDocumentLauncherWrapper: ExportDataUseCase.CreateDocumentLauncherWrapper
    ): ImportCSVDataUseCase {

        return ImportCSVDataUseCaseImpl(context, toastMaker, knownExerciseService, workoutService)
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