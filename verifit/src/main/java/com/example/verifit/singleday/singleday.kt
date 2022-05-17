package com.example.verifit.singleday

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.verifit.*
import com.example.verifit.common.*
import com.example.verifit.main.*
import com.example.verifit.singleday.dialog.DayListDialogViewModel
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi


@ExperimentalComposeUiApi
class Compose_DayActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DayListViewModel by viewModels {
        MockDayViewModelFactory(workoutService = WorkoutServiceSingleton.getWorkoutService(applicationContext),
            dateSelectStore = DateSelectStore,
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(context = applicationContext))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                DayListScreenWithAppbar(viewModel)
                Log.d("Diary","SetContent Finished")
            }
        }
    }
}
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayListScreenHilt() {
    DayListScreenWithAppbar(viewModel = hiltViewModel())
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DayListDialogHilt() {
    val viewModel : DayListDialogViewModel = hiltViewModel()
    val state = viewModel.viewState.collectAsState()
    Card(modifier = Modifier.padding(28.dp)) {
        Column() {
            Text(text = state.value.date,
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(all = 20.dp)
                    .clickable {
                        viewModel.onAction( com.example.verifit.singleday.dialog.UiAction.GoToMainViewPager)
                    },

                )
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
            DayListScreenWithoutAppBar()
        }
    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DayListScreenWithAppbar(@PreviewParameter(DayViewModelProvider::class) viewModel: DayListViewModel) {
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

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DayListScreenWithoutAppBar(
                               workoutExerciseClick: ((WorkoutExercise) -> Unit)? = null,
                               ) {
    val viewModel : DayListViewModel = hiltViewModel()
    val state = viewModel.viewState.collectAsState()
                ExercisesList(data = state.value.data, {
                    if (workoutExerciseClick != null) {
                        workoutExerciseClick.invoke(it)
                    } else  {viewModel.onAction(UiAction.GoToAddExercises(it))}
                })
}

class DayViewModelProvider(

) : PreviewParameterProvider<DayListViewModel> {
    override val values = sequenceOf(
            DayListViewModel(MockFetchDaysWorkoutsUseCase(getSampleViewPagerData().first().exercisesViewData), savedStateHandle = null
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
        return DayListViewModel(
                FetchDaysWorkoutsUseCaseImpl(workoutService,colorGetter = ColorGetterImpl(knownExerciseService)),
           savedStateHandle = null
        )
         as T
    }
}