package com.example.verifit.singleday

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.*
import com.example.verifit.diary.Compose_DiaryActivity
import com.example.verifit.exercises.Compose_ExercisesActivity
import com.example.verifit.main.*
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@ExperimentalComposeUiApi
class Compose_DayActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DayViewModel by viewModels {
        MockDayViewModelFactory(WorkoutServiceSingleton.getWorkoutService(applicationContext),DateSelectStore,KnownExerciseServiceSingleton.getKnownExerciseService(context = applicationContext))
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
@Preview

fun DayListScreen(@PreviewParameter(DayViewModelProvider::class) viewModel: DayViewModel) {
    val context = LocalContext.current
    LaunchedEffect(key1 = "ViewPagerScreen", block = {

        viewModel.oneShotEvents
                .onEach {
                    when (it) {
                        is OneShotEvents.GoToAddExercise -> {
                            val `in` = Intent(context, Compose_AddExerciseActivity::class.java)
                            `in`.putExtra("exercise", it.exerciseName)

                            context.startActivity(`in`)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                        is OneShotEvents.GoToExercisesList -> {

                            val intent = Intent(context, Compose_ExercisesActivity::class.java)
                            DateSelectStore.date_selected = it.dateString
                            context.startActivity(intent)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                        is OneShotEvents.GoToMainViewPager -> {
                            val intent = Intent(context, Compose_MainActivity::class.java)
                            context.startActivity(intent)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                        is OneShotEvents.GoToDiary -> {
                            val intent = Intent(context, Compose_DiaryActivity::class.java)
                            intent.putExtra("date", it.date)
                            context.startActivity(intent)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                    }
                }
                .collect()
    })
    val state = viewModel.viewState.collectAsState()
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            -> {
                viewModel.onAction(UiAction.OnResume)
            }
            else -> Unit
        }
    }
    MaterialTheme() {
        Scaffold(
                drawerContent = { /*...*/ },
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
                content = {
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
            DayViewModel(MockFetchDaysWorkoutsUseCase(getSampleViewPagerData().first().exercisesViewData)
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
                FetchDaysWorkoutsUseCaseImpl(workoutService,dateSelectStore,knownExerciseService)
        )
         as T
    }
}