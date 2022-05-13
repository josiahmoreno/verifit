package com.example.verifit

// for a `var` variable also add

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.verifit.addexercise.composables.*
import com.example.verifit.common.*
import com.example.verifit.di.assistedViewModel
import com.example.verifit.exercises.ExercisesListViewModel
import com.example.verifit.sets.SetStatsDialog
import com.example.verifit.sets.StatsDialog
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.FakeWorkoutService
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.data.LineData

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddExerciseScreenHilt(exerciseName: String?,
                          date: String?
                      ){
    val addExerciseViewModel: AddExerciseViewModel = assistedViewModel {
        AddExerciseViewModel.provideFactory(addExerciseViewModelFactory(),exerciseName!! , date!!)
    }
    AddExerciseScreen(viewModel = addExerciseViewModel)
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddExerciseScreen(exerciseName: String?,
                      date: String?,
                      navHostController: NavHostController){
    val context = LocalContext.current

    val addExerciseViewModel: AddExerciseViewModel = viewModel (factory =
    MviViewModelFactory(exerciseName!!, context,
        workoutService = WorkoutServiceSingleton.getWorkoutService(context = context),
            NavigateToHistoryDialogUseCase = NavigateToHistoryDialogUseCaseImpl(navHostController),
        NavigateToGraphDialogUseCase = NavigateToGraphDialogUseCaseImpl(navHostController),
        NavigateToTimerUseCase = NavigateToTimerUseCaseImpl(navHostController),
        NavigateToCommentUseCase = NavigateToCommentUseCaseImpl(navHostController),
        NavigateToDeleteSetDialogUseCase = NavigateToDeleteSetDialogUseCaseImpl(navigatorController = navHostController),
        date = date!!,
        ListenToCommentResultsUseCaseImpl(navHostController = navHostController)
    )
    )


    AddExerciseScreen(viewModel = addExerciseViewModel)
}
@ExperimentalComposeUiApi
class
Compose_AddExerciseActivity : AppCompatActivity() {
    // Helper Data Structure
    var exercise_name: String? = null
    lateinit var knownExerciseService: KnownExerciseService
    lateinit var workoutService: WorkoutService
    private val addExerciseViewModel: AddExerciseViewModel by viewModels {
        MviViewModelFactory(intent.getStringExtra("exercise")!!,
            this,
            workoutService = workoutService, NoOpNavigateToHistoryDialogUseCase(),
            NoOpNavigateToGraphDialogUseCase(),
            NoOpNavigateToTimerUseCase(),
            NoOpNavigateToCommentUseCase(),
            NoOpNavigateToDeleteSetDialogUseCase(),
            DateSelectStore.date_selected,
            //navHostController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("comment")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        throw Exception()
        super.onCreate(savedInstanceState)
        knownExerciseService = PrefKnownExerciseServiceImpl(applicationContext = applicationContext)
        workoutService = WorkoutServiceSingleton.getWorkoutService(context = applicationContext)
        setContent {
            AddExerciseScreen(addExerciseViewModel)
        }
    }
}
    private val MyLightColorPalette = lightColors(
        primary = Color(0xff0074bd),
        primaryVariant = Color.Green,
        secondary = Color.Green,
        secondaryVariant = Color.Green
    )



    @ExperimentalComposeUiApi
    @OptIn(ExperimentalMaterialApi::class)
    @Preview
    @Composable
    fun AddExerciseScreen(@PreviewParameter(MviPreviewProvider::class) viewModel: AddExerciseViewModel) {
        val context = LocalContext.current
        val state by viewModel.viewState.collectAsState()
        val showDeleteDialog = remember { mutableStateOf(false) }
        val showCommentDialog = remember { mutableStateOf(false) }
        val showHistoryDialog = remember { mutableStateOf(false) }
        val showTimerDialog = remember { mutableStateOf(false) }
        val showGraphDialog = remember { mutableStateOf(false) }
        val showStatsDialog = remember { mutableStateOf(false) }
        val showSetStatsDialog = remember { mutableStateOf(false) }
        val lineData = remember { mutableStateOf<LineData?>(null) }

        MaterialTheme(colors = MyLightColorPalette) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        title = {

                            Text(text = state.exerciseName ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis) // titl
                        },
                        actions = {
                            IconButton(onClick = { viewModel.onAction(AddExerciseViewModel.UiAction.ShowHistory) }) {
                                Icon(Icons.Filled.SettingsBackupRestore, "history")
                            }
                            IconButton(onClick = { viewModel.onAction(AddExerciseViewModel.UiAction.ShowGraph) }) {
                                Icon(Icons.Filled.Poll, "graph")
                            }
                            IconButton(onClick = { viewModel.onAction(AddExerciseViewModel.UiAction.ShowTimer) }) {
                                Icon(Icons.Filled.Alarm, "timer")
                            }
                            IconButton(onClick = { viewModel.onAction(AddExerciseViewModel.UiAction.ShowComments) }) {
                                Icon(Icons.Filled.Comment, "comment")
                            }
                        }
                    )
                },
                content = { padding ->
                    Column {
                        Column(modifier = Modifier.padding(Dp(16.0f))) {
                            Text("Weight:", fontSize = 20.sp)
                            // use the material divider
                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            //incrementable
                            Incrementable(
                                decrement = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.WeightDecrement)
                                },
                                increment = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.WeightIncrement)
                                },
                                amount = state.weightText,
                                onTextChanged = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.OnWeightChange(it))
                                },
                                options = IncrementableOptions(regex = "^([0-9]+\\.?[0-9]*|[0-9]*\\.[0-9]+)?$")
                            )
                            Spacer(Modifier.padding(top = 10.dp))
                            Text("Reps:", fontSize = 20.sp)
                            // use the material divider
                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            Incrementable(
                                decrement = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.RepDecrement)
                                },
                                increment = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.RepIncrement)
                                },
                                amount = state.repText,
                                onTextChanged = {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.OnRepChange(it))
                                },
                                options = IncrementableOptions(regex = "^([0-9]+)?$")
                            )
                            Spacer(Modifier.padding(top = 20.dp))
                            Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 0.dp),
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.onAction(AddExerciseViewModel.UiAction.SaveExercise(
                                            state.weightText,
                                            state.repText,
                                            state.exerciseName!!
                                        )
                                        )
                                    },
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(end = 2.5.dp)
                                            .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                                ) {
                                    Text("Save")
                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onAction(AddExerciseViewModel.UiAction.Clear)
                                    },
                                    border = BorderStroke(1.dp, MaterialTheme.colors.primary),

                                    shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(start = 2.5.dp)
                                    //.clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                                ) {
                                    Text(state.clearButtonText)
                                }
                            }
                        }// ends here
                        val list = state.workoutSets.observeAsState(initial = state.workoutSets.value!! )
                        Spacer(Modifier.padding(top = 4.dp))
                        LazyColumn {
                            items(list.value.sets ?: emptyList()) { workoutSetItem ->
                                WorkoutSetRow(workoutSetItem) {
                                    viewModel.onAction(AddExerciseViewModel.UiAction.WorkoutClick(
                                        workoutSetItem))
                                }
                            }
                        }
                        //TimerAlertDialog(showTimerDialog, state, viewModel)
                        val exercise = remember {
                            mutableStateOf<WorkoutExercise?>(null)
                        }
                        val set = remember {
                            mutableStateOf<WorkoutSet?>(null)
                        }
                        History2Dialog(state, showHistoryDialog, { workoutExercise ->
                            exercise.value = workoutExercise
                            showStatsDialog.value = true
                        }, { workoutSet ->
                            set.value = workoutSet
                            showSetStatsDialog.value = true
                        }, state.exerciseName)
                        StatsDialog(showStatsDialog, exercise.value)
                        SetStatsDialog(showSetStatsDialog, set.value)
                        GraphDialog(showGraphDialog, lineData = lineData.value)
                        CommentDialog(showCommentDialog, state.commentText,
                            save = {
                                viewModel.onAction(AddExerciseViewModel.UiAction.SaveComment(it))
                            },
                            clear = {
                                viewModel.onAction(AddExerciseViewModel.UiAction.ClearComment)
                            }
                        )
                        DeleteDialog(showDeleteDialog,
                            yes = {
                                viewModel.onAction(AddExerciseViewModel.UiAction.YesDelete)
                            },
                            no = {
                                viewModel.onAction(AddExerciseViewModel.UiAction.NoDelete)
                            })
                    }
                }
            )
        }
    }



class SampleObjProvider : PreviewParameterProvider<WorkoutSet> {
    override val values = sequenceOf(
        WorkoutSet("", "", "", 100.0, 12.0),
    )
    override val count: Int = values.count()
}


class MviViewModelFactory(
    private val exercise_name: String,
    private val applicationContext: Context,
    private val workoutService: WorkoutService,
    private val NavigateToHistoryDialogUseCase: NavigateToHistoryDialogUseCase,
    private val NavigateToGraphDialogUseCase: NavigateToGraphDialogUseCase,
    private val NavigateToTimerUseCase: NavigateToTimerUseCase,
    private val NavigateToCommentUseCase: NavigateToCommentUseCase,
    private val NavigateToDeleteSetDialogUseCase: NavigateToDeleteSetDialogUseCase,
    private val date: String,
    val liveData: ListenToCommentResultsUseCase? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddExerciseViewModel(
                localDataSource = workoutService,
            KnownExerciseServiceSingleton.getKnownExerciseService(applicationContext),
            NavigateToHistoryDialogUseCase,
            NavigateToGraphDialogUseCase = NavigateToGraphDialogUseCase,
            NavigateToTimerUseCase = NavigateToTimerUseCase,
            NavigateToCommentUseCase = NavigateToCommentUseCase,
            NavigateToDeleteSetDialogUseCase = NavigateToDeleteSetDialogUseCase,
            exercise_name,
            date,
            liveData
        ) as T
    }
}

class MviPreviewProvider : PreviewParameterProvider<AddExerciseViewModel> {
    override val values: Sequence<AddExerciseViewModel>
        get() = sequenceOf(AddExerciseViewModel(FakeWorkoutService(),
            DefaultKnownExercise(),
            NoOpNavigateToHistoryDialogUseCase(),
            NoOpNavigateToGraphDialogUseCase(),
            NoOpNavigateToTimerUseCase(),
            NoOpNavigateToCommentUseCase(),
            NoOpNavigateToDeleteSetDialogUseCase(),
            "Flat Barbell Bench Press",
            "yo",NoOpListenToCommentResultsUseCase())
        )

}
