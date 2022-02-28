package com.example.verifit.addexercise.composables

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.tooling.preview.Preview

@ExperimentalMaterialApi
@Preview
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