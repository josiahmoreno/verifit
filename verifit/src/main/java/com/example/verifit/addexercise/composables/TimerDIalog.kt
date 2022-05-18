package com.example.verifit.addexercise.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.verifit.timer.TimerServiceWrapperImpl
import com.example.verifit.timer.TimerViewModel
import com.example.verifit.timer.UiAction
//
//@ExperimentalMaterialApi
//@Composable
//fun TimerAlertDialog(showTimerDialog : MutableState<Boolean>, state: AddExerciseViewState, viewModel: AddExerciseViewModel){
//    if (showTimerDialog.value) {
//
//        AlertDialog(
//            onDismissRequest = {
//                // Dismiss the dialog when the user clicks outside the dialog or on the back
//                // button. If you want to disable that functionality, simply use an empty
//                // onCloseRequest.
//                showTimerDialog.value = false
//            },
//            title = {
//                Text(text = "Timer", color = MaterialTheme.colors.primary)
//            },
//            text = {
//                Incrementable(
//                    amount = state.secondsLeftString,
//                    decrement = {viewModel.onAction(AddExerciseViewModel.UiAction.MinusSeconds(state.secondsLeftString))},
//                    increment = {viewModel.onAction(AddExerciseViewModel.UiAction.PlusSeconds(state.secondsLeftString))},
//                    onTextChanged = {viewModel.onAction(AddExerciseViewModel.UiAction.OnSecondsChange(it)) } ,
//
//                    options = IncrementableOptions(regex = "^([0-9]+)?$")
//                )
//            },
//            confirmButton = {
//                TextButton(
//
//                    onClick = {
//                        viewModel.onAction(AddExerciseViewModel.UiAction.StartTimer(state.secondsLeftString))
//                    }) {
//                    Text(state.timerButtonText)
//                }
//            },
//            dismissButton = {
//                TextButton(
//
//                    onClick = {
//                        viewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer)
//                    }) {
//                    Text("Reset")
//                }
//            }
//        )
//    }
//}
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
fun TimerContent(showStart:Boolean,
                 pauseTimer: (()-> Unit),
                 secondsLeftString:String, decrement: (()->Unit),
                 increment: (()->Unit), onTextChanged: ((String) -> Unit),
                 startTimer: (() -> Unit),
                 resetTimer: (()->Unit),
                 cancelTimer: (()->Unit),
                 ){

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

                if(showStart){
                    TextButton(
                        onClick = {
                            startTimer()
                        }) {
                        Text("Start")
                    }
                } else {
                    TextButton(
                        onClick = {
                            pauseTimer()
                        }) {
                        Text("Pause")
                    }
                    TextButton(
                        onClick = {
                            cancelTimer()
                            //viewModel.onAction(AddExerciseViewModel.UiAction.ResetTimer)
                        }) {
                        Text("Cancel")
                    }
                }

            }

        
    }
}

@ExperimentalMaterialApi
@Composable
fun TimerContentHilt(){
    Card(modifier = Modifier.padding(28.dp)) {
        TimerContent(
               hiltViewModel()
        )
    }
}
@ExperimentalMaterialApi
@Composable
fun TimerContent(){
    Card(modifier = Modifier.padding(28.dp)) {
        TimerContent(
            TimerViewModel(TimerServiceWrapperImpl(CountDownTimerService(LocalContext.current), LocalContext.current))
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun TimerContent(viewModel: TimerViewModel){
    val state = viewModel.viewState.collectAsState()
    DisposableEffect(key1 = viewModel) {
        onDispose {
            viewModel.onAction(UiAction.OnDispose)
        }

    }

    TimerContent(
        showStart = state.value.showStart,
        pauseTimer = {
            viewModel.onAction(UiAction.PauseTimer)
        },
        secondsLeftString = state.value.secondsLeft,
        decrement = {
            viewModel.onAction(UiAction.DecrementSeconds(state.value.secondsLeft))
        },
        increment = {
            viewModel.onAction(UiAction.IncrementSeconds(state.value.secondsLeft))
        },
        onTextChanged = {
            viewModel.onAction(UiAction.OnTextChanged(it))
        },
        startTimer = {
            viewModel.onAction(UiAction.StartTimer)
        },
        resetTimer = {
            viewModel.onAction(UiAction.ResetTimer)
            //viewModel.onAction(UiAction.)
        }, cancelTimer = {
            viewModel.onAction(UiAction.CancelTimer)
        }
    )
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun TimerContentPreview(){

    TimerContent(
        showStart = true,
        pauseTimer = {},
        secondsLeftString = "180",
        decrement = {},
        increment = {},
        onTextChanged = {},
        startTimer = {},
        resetTimer = {},
        cancelTimer = {}
    )
}