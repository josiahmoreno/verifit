package com.example.verifit

// for a `var` variable also add

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class
Compose_AddExerciseActivity : AppCompatActivity() {
    // Helper Data Structures

    var exercise_name: String? = null



    private val mviViewModel : MviViewModel by viewModels {
        MviViewModelFactory(intent.getStringExtra("exercise"),this)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContent {

                    AddExerciseScreen(mviViewModel)
                }

        //setTheme(R.style.AppTheme)


    }

private val MyLightColorPalette = lightColors(
    primary = Color(0xff0074bd),
    primaryVariant = Color.Green,
    secondary = Color.Green,
    secondaryVariant = Color.Green
)

    @OptIn(ExperimentalMaterialApi::class)
    @Preview
    @Composable
    fun AddExerciseScreen(@PreviewParameter(MviPreviewProvider::class) viewModel: MviViewModel) {
        val context = LocalContext.current
        val state by viewModel.viewState.collectAsState()
        val openDialog = remember { mutableStateOf(false)  }
        val showTimerDialog = remember { mutableStateOf(false)  }
        LaunchedEffect("SIDE_EFFECTS_KEY") {
            viewModel.oneShotEvents.onEach { effect ->
                when (effect) {
                    is MviViewModel.OneShotEvent.ShowCommentDialog -> TODO()
                    MviViewModel.OneShotEvent.ShowDeleteDialog -> TODO()
                    is MviViewModel.OneShotEvent.ShowGraphDialog -> TODO()
                    is MviViewModel.OneShotEvent.ShowHistoryDialog -> {openDialog.value = true}
                    is MviViewModel.OneShotEvent.ShowTimerDialog -> {showTimerDialog.value = true}
                    is MviViewModel.OneShotEvent.Toast -> Toast.makeText(context,effect.toast,Toast.LENGTH_SHORT).show()
                }
            }.collect()
        }
        MaterialTheme(colors = MyLightColorPalette) {


            Scaffold(
                drawerContent = { /*...*/ },
                topBar = {
                    TopAppBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        title = {

                            Text(text = state.exerciseName ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) // titl
                        },
                        actions = {
                            IconButton(onClick = {viewModel.onAction(MviViewModel.UiAction.ShowHistory)}) {
                                Icon(Icons.Filled.SettingsBackupRestore, "history")
                            }
                            IconButton(onClick = {viewModel.onAction(MviViewModel.UiAction.ShowGraph)}) {
                                Icon(Icons.Filled.Poll, "graph")
                            }
                            IconButton(onClick ={viewModel.onAction(MviViewModel.UiAction.ShowTimer)}) {
                                Icon(Icons.Filled.Alarm, "timer")
                            }
                            IconButton(onClick = {viewModel.onAction(MviViewModel.UiAction.ShowComments)}) {
                                Icon(Icons.Filled.Comment, "comment")
                            }
                        }
                    )
                },
                content = {
                    Column {
                        Column(modifier = Modifier.padding(Dp(16.0f))) {
                            Text("Weight:")
                            // use the material divider
                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            //incrementable
                            Incrementable(decrement = {
                                viewModel.onAction(MviViewModel.UiAction.WeightDecrement)
                            },increment = {
                                viewModel.onAction(MviViewModel.UiAction.WeightIncrement)
                            },amount =   state.weightText
                            )
                            /*
                            Row {
                                IconButton(onClick = {
                                    viewModel.onAction(MviViewModel.UiAction.WeightDecrement)
                                }) {
                                    Icon(Icons.Filled.Remove, "plus one")
                                }
                                //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                                TextField(
                                    value = state.weightText,
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                    onValueChange = { },

                                    )
                                IconButton(onClick = {
                                    viewModel.onAction(MviViewModel.UiAction.WeightIncrement)
                                }) {
                                    Icon(Icons.Filled.Add, "plus one")
                                }
                            }

                             */
                            Text("Reps:")
                            // use the material divider
                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            Row {
                                IconButton(onClick = {
                                    viewModel.onAction(MviViewModel.UiAction.RepDecrement)
                                }) {
                                    Icon(Icons.Filled.Remove, "plus one")
                                }
                                //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                                TextField(
                                    value = state.repText,
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                    onValueChange = { },

                                    )
                                IconButton(onClick = {
                                    viewModel.onAction(MviViewModel.UiAction.RepIncrement)
                                }) {
                                    Icon(Icons.Filled.Add, "plus one")
                                }

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.onAction(MviViewModel.UiAction.SaveExercise(
                                            state.weightText,
                                            state.repText,
                                            state.exerciseName!!,
                                            MainActivity.getExerciseCategory(exercise_name),
                                            MainActivity.getDayPosition(MainActivity.date_selected)
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
                                    onClick = {},
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
                        val list = state.workoutSets.observeAsState(listOf())
                        LazyColumn {
                            items(list.value) { workoutSetItem ->
                                ExerciseRow(workoutSetItem) {
                                    viewModel.onAction(MviViewModel.UiAction.WorkoutClick(
                                        workoutSetItem))
                                }
                            }
                        }
                        if (openDialog.value) {

                            AlertDialog(
                                onDismissRequest = {
                                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                                    // button. If you want to disable that functionality, simply use an empty
                                    // onCloseRequest.
                                    openDialog.value = false
                                },
                                title = {
                                    Text(text = "Dialog Title")
                                },
                                text = {
                                    Text("Here is a text ")
                                },
                                confirmButton = {
                                    Button(

                                        onClick = {
                                            openDialog.value = false
                                        }) {
                                        Text("This is the Confirm Button")
                                    }
                                },
                                dismissButton = {
                                    Button(

                                        onClick = {
                                            openDialog.value = false
                                        }) {
                                        Text("This is the dismiss Button")
                                    }
                                }
                            )
                        }
                        if (showTimerDialog.value) {

                            AlertDialog(
                                onDismissRequest = {
                                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                                    // button. If you want to disable that functionality, simply use an empty
                                    // onCloseRequest.
                                    showTimerDialog.value = false
                                },
                                title = {
                                    Text(text = "Timer", color = MaterialTheme.colors.primary)
                                },
                                text = {
                                    Text("Here is a timer countdown${state.secondsLeftString} ")
                                },
                                confirmButton = {
                                    TextButton(

                                        onClick = {
                                             viewModel.onAction(MviViewModel.UiAction.StartTimer("10"))
                                        }) {
                                        Text("Confirm ")
                                    }
                                },
                                dismissButton = {
                                    TextButton(

                                        onClick = {
                                            showTimerDialog.value = false
                                        }) {
                                        Text("dismiss")
                                    }
                                }
                            )
                        }
                    }
                }
            )

        }

    }
}

@Preview
@Composable
fun Incrementable( amount : String = "4.0", decrement: (()->Unit)? = null, increment: (()->Unit)? = null){
    Row {
        IconButton(onClick = {
                decrement?.invoke()
        }) {
            Icon(Icons.Filled.Remove, "plus one")
        }
        //textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
        TextField(
            value = amount,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 28.sp, fontWeight = FontWeight.Bold),
            onValueChange = { },

            )
        IconButton(onClick = {
            increment?.invoke()
        }) {
            Icon(Icons.Filled.Add, "plus one")
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun ExerciseRow(@PreviewParameter(SampleObjProvider::class) workoutSet: WorkoutSet, click :(()-> Unit)? = null ){
    Card(onClick = {click?.invoke()}, elevation = 0.dp) {
        Column {


            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(text = workoutSet.weight.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                Text(text = "kg",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(start = 5.dp, top = 15.dp))
                Spacer(modifier = Modifier.weight(1.0f))
                Text(text = workoutSet.reps.toString(),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                Text(text = "reps",
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier.padding(end = 40.dp, top = 15.dp))
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

class SampleObjProvider: PreviewParameterProvider<WorkoutSet> {
    override val values = sequenceOf(WorkoutSet("","","",100.0,12.0),
        )
    override val count: Int = values.count()
}



class MviViewModelFactory(private val exercise_name: String?, private val applicationContext: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MviViewModel(PrefWorkoutServiceImpl(exercise_name, applicationContext = applicationContext), TimerServiceImpl(applicationContext),exercise_name) as T
    }




}




    


class MviPreviewProvider : PreviewParameterProvider<MviViewModel> {
    override val values: Sequence<MviViewModel>
        get() = sequenceOf(MviViewModel(FakeWorkoutService(),FakeTimer(),"Flat Barbell Bench Press"))

}
