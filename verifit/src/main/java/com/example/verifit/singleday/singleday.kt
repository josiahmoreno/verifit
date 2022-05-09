package com.example.verifit.singleday

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.verifit.*
import com.example.verifit.common.*
import com.example.verifit.main.*
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi


@ExperimentalComposeUiApi
class Compose_DayActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DayViewModel by viewModels {
        MockDayViewModelFactory(workoutService = WorkoutServiceSingleton.getWorkoutService(applicationContext),
            dateSelectStore = DateSelectStore,
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(context = applicationContext))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                DayListScreen(viewModel)
                Log.d("Diary","SetContent Finished")
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayListScreen(navController: NavHostController, date: String) {
    DayListScreen(viewModel = DayViewModel(
        fetchDaysWorkoutsUseCase = FetchDaysWorkoutsUseCaseImpl(workoutService = WorkoutServiceSingleton.getWorkoutService(
            LocalContext.current),
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(context = LocalContext.current),
            colorGetter = ColorGetterImpl(KnownExerciseServiceSingleton.getKnownExerciseService(context = LocalContext.current))
        ),
        NavigateToExercisesListUseCase = NavigateToExercisesListUseCaseImpl(navHostController = navController),
        NavigateToAddExerciseUseCase = NavigateToAddExerciseUseCaseImpl(navHostController = navController,
             root = "diary_list?date=${ navController.currentBackStackEntry?.arguments?.getString("date")}"),
        NavigateToDiaryUseCase = NavigateToDiaryListUseCaseImpl(navHostController = navController),
        NavigateToViewPagerUseCase = NavigateToViewPagerUseCaseImpl(navHostController = navController),
        date
    ))
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DayListScreen(@PreviewParameter(DayViewModelProvider::class) viewModel: DayViewModel) {
    val context = LocalContext.current
    val state = viewModel.viewState.collectAsState()
    MaterialTheme() {
        Scaffold(
                topBar = {
                    TopAppBar(
                            backgroundColor = MaterialTheme.colors.primary,
                            title = {

                                Text(text = state.value.date,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis) // titl
                            },
                            actions = {
                                IconButton(onClick = {
                                    viewModel.onAction(UiAction.GoToMainViewPager)
                                }) {
                                    Icon(Icons.Filled.Home, "home")
                                }
                                IconButton(onClick = {
                                    //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                    viewModel.onAction(UiAction.GoToDiaryWithDay)
                                }) {
                                    Icon(Icons.Filled.Ballot, "diary")
                                }
                                IconButton(onClick = {
                                    viewModel.onAction(UiAction.GoToExercisesList)
                                }) {
                                    Icon(Icons.Filled.Add, "add to exercise")
                                }
                            }
                    )
                },
                content = { padding ->
                    ExercisesList(data = state.value.data, {
                        viewModel.onAction(UiAction.GoToAddExercises(it))
                    })
                },
                bottomBar = {

                }
        )
    }

}

class DayViewModelProvider(

) : PreviewParameterProvider<DayViewModel> {
    override val values = sequenceOf(
            DayViewModel(MockFetchDaysWorkoutsUseCase(getSampleViewPagerData().first().exercisesViewData), date = ""
            )
    )
}

// public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight,String Comment)
class MockDayViewModelFactory(
    val workoutService: WorkoutService,
    val dateSelectStore: DateSelectStore,
    val knownExerciseService: KnownExerciseService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DayViewModel(
                FetchDaysWorkoutsUseCaseImpl(workoutService,knownExerciseService,colorGetter = ColorGetterImpl(knownExerciseService)),
            date = ""
        )
         as T
    }
}