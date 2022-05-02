package com.example.verifit.addexercise.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verifit.timer.TimerViewModel

@ExperimentalMaterialApi
@Composable
fun TimerAlertDialog(showTimerDialog : MutableState<Boolean>, state: AddExerciseViewState, viewModel: AddExerciseViewModel){
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
                Incrementable(
                    amount = state.secondsLeftString,
                    decrement = {viewModel.onAction(AddExerciseViewModel.UiAction.MinusSeconds(state.secondsLeftString))},
                    increment = {viewModel.onAction(AddExerciseViewModel.UiAction.PlusSeconds(state.secondsLeftString))},
                    onTextChanged = {viewModel.onAction(AddExerciseViewModel.UiAction.OnSecondsChange(it)) } ,

                    options = IncrementableOptions(regex = "^([0-9]+)?$")
                )
            },
            confirmButton = {
                TextButton(

                    onClick = {
                        viewModel.onAction(AddExerciseViewModel.UiAction.StartTimer(state.secondsLeftString))
                    }) {
                    Text(state.timerButtonText)
                }
            },
            dismissButton = {
                TextButton(

                    onClick = {
                        viewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer)
                    }) {
                    Text("Reset")
                }
            }
        )
    }
}
@ExperimentalMaterialApi
@Composable
fun TimerAlertDialog(timerButtonText: String, secondsLeftString:String, decrement: (()->Unit), increment: (()->Unit), onTextChanged: ((String) -> Unit), startTimer: (() -> Unit),
resetTimer: (()->Unit), onDismiss: (()->Unit))
{
        AlertDialog(
            onDismissRequest = {
                               onDismiss()
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                //showTimerDialog.value = false
            },
            title = {
                Text(text = "Timer", color = MaterialTheme.colors.primary)
            },
            text = {
                Incrementable(
                    amount = secondsLeftString,
                    decrement = {
                                decrement()
                        //viewModel.onAction(AddExerciseViewModel.UiAction.MinusSeconds(state.secondsLeftString))
                                },
                    increment = {
                        increment()
                        //viewModel.onAction(AddExerciseViewModel.UiAction.PlusSeconds(state.secondsLeftString))
                                },
                    onTextChanged = {
                        onTextChanged(it)
                        //viewModel.onAction(AddExerciseViewModel.UiAction.OnSecondsChange(it))
                                    } ,

                    options = IncrementableOptions(regex = "^([0-9]+)?$")
                )
            },
            confirmButton = {
                TextButton(

                    onClick = {
                        startTimer()
                        //viewModel.onAction(AddExerciseViewModel.UiAction.StartTimer(state.secondsLeftString))
                    }) {
                    Text(timerButtonText)
                }
            },
            dismissButton = {
                TextButton(

                    onClick = {
                        resetTimer()
                        //viewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer)
                    }) {
                    Text("Reset")
                }
            }
        )
    }


@ExperimentalMaterialApi
@Composable
fun TimerContent(timerButtonText: String, secondsLeftString:String, decrement: (()->Unit), increment: (()->Unit), onTextChanged: ((String) -> Unit), startTimer: (() -> Unit),
                 resetTimer: (()->Unit)){

    Column() {
        Text(text = "Timer",
            color = MaterialTheme.colors.primary,
            fontSize = 22.sp,
            modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        Incrementable(
            amount = secondsLeftString,
            decrement = {
                decrement()
                //viewModel.onAction(AddExerciseViewModel.UiAction.MinusSeconds(state.secondsLeftString))
            },
            increment = {
                increment()
                //viewModel.onAction(AddExerciseViewModel.UiAction.PlusSeconds(state.secondsLeftString))
            },
            onTextChanged = {
                onTextChanged(it)
                //viewModel.onAction(AddExerciseViewModel.UiAction.OnSecondsChange(it))
            } ,
            options = IncrementableOptions(regex = "^([0-9]+)?$")
        )

            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End){
                TextButton(
                    onClick = {
                        resetTimer()
                        //viewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer)
                    }) {
                    Text("Reset")
                }
                TextButton(
                    onClick = {
                        startTimer()
                    }) {
                    Text(timerButtonText)
                }
            }

        
    }
}

@ExperimentalMaterialApi
@Composable
fun TimerContent(viewModel: TimerViewModel){
    val state = viewModel.viewState.collectAsState()
    TimerContent(
        timerButtonText = state.value.timerButtonText,
        secondsLeftString = state.value.secondsLeft,
        decrement = {},
        increment = {},
        onTextChanged = {},
        startTimer = {},
        resetTimer = {}
    )
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun TimerContentPreview(){

    TimerContent(
        timerButtonText = "Start",
        secondsLeftString = "180",
        decrement = {},
        increment = {},
        onTextChanged = {},
        startTimer = {},
        resetTimer = {}
    )
}