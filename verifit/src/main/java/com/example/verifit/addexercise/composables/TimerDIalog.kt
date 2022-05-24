package com.example.verifit.addexercise.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.verifit.timer.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
//                 vibrate: Boolean = false,
//                 sound: Boolean = false,
//                 autoStart: Boolean = false
                 vibrate: MutableState<Boolean> = remember{mutableStateOf(false)},
                 onVibrateChanged: ((Boolean) -> Unit),
                 sound: MutableState<Boolean> = remember{mutableStateOf(false)},
                 onSoundChanged: ((Boolean) -> Unit),
                 autoStart: MutableState<Boolean> = remember{mutableStateOf(false)},
                         onAutoStartChanged: ((Boolean) -> Unit)
                 ){

//    val vibrate2: MutableState<Boolean> = remember{mutableStateOf(vibrate)}
//                 val sound2: MutableState<Boolean> = remember{mutableStateOf(sound)}
//                 val autoStart2: MutableState<Boolean> = remember{mutableStateOf(autoStart)}
    Column {
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


        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = vibrate.value,
                onCheckedChange = { vibrate.value = it
                    onVibrateChanged(it)
                                  },
                enabled = true,
            )
            Text(text = "Vibrate")
        }
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

            Checkbox(
                checked = sound.value,
                onCheckedChange = { sound.value = it
                    onSoundChanged(it)
                                  },
                enabled = true,
            )
            Text(text = "Sound")
        }
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = autoStart.value,
                onCheckedChange = { autoStart.value = it
                                  onAutoStartChanged(it)
                                  },
                enabled = true,
            )
            Text(text = "AUTO START")
        }
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
            TimerViewModel(TimerServiceWrapperImpl(CountDownTimerService(LocalContext.current), LocalContext.current), FetchTimerViewSettingsImpl())
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
    val vibrate: MutableState<Boolean> = remember{mutableStateOf(state.value.vibrate)}
    val sound: MutableState<Boolean> = remember{mutableStateOf(state.value.sound)}
    val autoStart: MutableState<Boolean> = remember{mutableStateOf(state.value.autoStart)}


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
        },
        vibrate = vibrate,
        onVibrateChanged = {
            viewModel.onAction(UiAction.OnVibrateCheck(it))
        },
        sound = sound,
        onSoundChanged = {
            viewModel.onAction(UiAction.OnSoundChanged(it))
        },
        autoStart = autoStart,
        onAutoStartChanged = {
            viewModel.onAction(UiAction.OnAutoStartChanged(it))
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
        cancelTimer = {},
        onVibrateChanged = {},
        onSoundChanged = {},
        onAutoStartChanged = {

        }
    )
}