
package com.example.verifit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.verifit.MviPreviewProvider
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.example.verifit.addexercise.FakeTimer
import com.example.verifit.addexercise.FakeWorkoutService
import com.example.verifit.addexercise.MviViewModel
import com.example.verifit.addexercise.composables.ViewState
import com.example.verifit.addexercise.composables.WorkoutSetRow
import java.text.SimpleDateFormat
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview
@Composable
fun HistoryDialog(@PreviewParameter(MviPreviewViewStateProvider::class) state2: ViewState,
                  showDialog: MutableState<Boolean> = remember{mutableStateOf(false)},
                  click : (()-> Unit)? = null

){
    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (showDialog.value) {
        val list : State<List<WorkoutExercise>> = remember{mutableStateOf(state2.history ?: ArrayList())}

        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                showDialog.value = false
            },
            title = {
                Text(text = "History", color = MaterialTheme.colors.primary)
            },
            text = {
                LazyColumn(
                    //modifier = Modifier.verticalScroll(
                    //rememberScrollState())
                ) {
                    list.value.forEach { workoutExercise ->
                        //Card{
                        item{
                            Card {
                                Column {
                                    Text("wtf ${workoutExercise.date}",
                                            fontSize = 26.sp,
                                            modifier = Modifier.padding(start = 15.dp,
                                                    end = 15.dp,
                                                    top = 10.dp)
                                    )

                                    workoutExercise.sets.forEach { set ->
                                        WorkoutSetRow(set) {
//                                        //viewModel?.onAction(MviViewModel.UiAction.WorkoutClick(set))
                                        }
                                    }
                                }
                                }

                        }


                       // }

//                            LazyColumn {
//                                items(workoutExercise.sets) { set ->
//                                    WorkoutSetRow(set) {
//                                        //viewModel?.onAction(MviViewModel.UiAction.WorkoutClick(set))
//                                    }
//                                }
                       // }

                    }
                }
            },
            confirmButton = {

            },
            dismissButton = {

            }
        )
    }
}

class MviPreviewViewStateProvider : PreviewParameterProvider<ViewState> {
    override val values = sequenceOf(
        ViewState(
            exerciseName = "Flat Barbell Bench Press",
            secondsLeftLiveData = MutableLiveData(),
            workoutSets = MutableLiveData(ArrayList()),
            history = arrayListOf(WorkoutExercise().apply {
                date = LocalDateTime.now().toString()
                sets = arrayListOf(
                    WorkoutSet("1111", "mememe", "", 1.0, 111.0),
                    WorkoutSet("1222", "mememe", "", 1.1, 122.0),
                        WorkoutSet("1222", "mememe", "", 1.2, 133.0),
                                WorkoutSet("1222", "mememe", "", 1.2, 144.0)
                )
            },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(
                            WorkoutSet("Yoyoyo", "mememe", "", 2.0, 211.0),
                            WorkoutSet("Yoyoyo", "mememe", "", 2.1, 222.0),
                                    WorkoutSet("Yoyoyo", "mememe", "", 2.2, 233.0)
                    )
                },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(
                            WorkoutSet("Yoyoyo", "mememe", "", 3.0, 311.0),
                            WorkoutSet("Yoyoyo", "mememe", "", 3.1, 322.0),
                                    WorkoutSet("Yoyoyo", "mememe", "", 3.2, 333.0),
                            WorkoutSet("Yoyoyo", "mememe", "", 3.3, 344.0)
                    )
                }
            )
        ),
        ViewState(
            exerciseName = "Bumpy Dumbbell Bench Squish",
            secondsLeftLiveData = MutableLiveData(),
            workoutSets = MutableLiveData(ArrayList()),
            history = arrayListOf(WorkoutExercise().apply {
                date = LocalDateTime.now().toString()
                sets = arrayListOf(
                    WorkoutSet("1111", "mememe", "", 4.0, 444.0),
                    WorkoutSet("1222", "mememe", "", 4.0, 444.0)

                )
            },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(WorkoutSet("2222", "mememe", "", 5.0, 555.0))
                },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(WorkoutSet("333", "mememe", "", 6.0, 666.0))
                }
            ),

            ),
        ViewState(
            exerciseName = "Bumpy Dumbbell Bench Honky",
            secondsLeftLiveData = MutableLiveData(),
            workoutSets = MutableLiveData(ArrayList()),
            history = arrayListOf(WorkoutExercise().apply {
                date = LocalDateTime.now().toString()
                sets = ArrayList()
            },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(WorkoutSet("Yoyoyo", "mememe", "", 10.0, 200.0))
                },
                WorkoutExercise().apply {
                    date = LocalDateTime.now().toString()
                    sets = arrayListOf(WorkoutSet("Yoyoyo", "mememe", "", 10.0, 200.0))
                }
            ),

            )
    )
    override val count: Int = values.count()

}



