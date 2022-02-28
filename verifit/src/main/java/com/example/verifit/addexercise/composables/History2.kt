
package com.example.verifit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.verifit.addexercise.composables.AddExerciseViewState
import com.example.verifit.addexercise.composables.WorkoutSetRow
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview
@Composable
fun History2Dialog(
        @PreviewParameter(MviPreviewViewStateProvider::class) state2: AddExerciseViewState,
        showDialog: MutableState<Boolean> = remember { mutableStateOf(true) },
        exerciseClick: ((WorkoutExercise) -> Unit)? = null,
        setClick: ((WorkoutSet) -> Unit)? = null,
        title: String? = "null",

        ){
    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (showDialog.value) {
        val list : State<List<WorkoutExercise>> = remember{mutableStateOf(state2.history ?: ArrayList())}

        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                showDialog.value = false
            },

            content = {
                Card(modifier = Modifier.padding(28.dp)) {
                    Column {


                        Text(text = "${state2.exerciseName}",
                            color = MaterialTheme.colors.primary,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(all = 20.dp)
                        )
                        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {

                            items(list.value) { workoutExercise ->
                                Spacer(modifier = Modifier.padding(top = 10.dp))
                                Card(elevation = 4.dp, modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                                    Column {
                                        var date1 : Date? = null
                                        try {
                                            date1 =
                                                SimpleDateFormat("yyyy-MM-dd").parse(workoutExercise.date)
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                        }

                                        val dateFormat: DateFormat =
                                            SimpleDateFormat("EEEE, MMM dd")
                                        val strDate = dateFormat.format(date1)
                                        Text(strDate,
                                            fontSize = 26.sp,
                                            modifier = Modifier.padding(start = 15.dp,
                                                end = 15.dp,
                                                top = 10.dp,
                                                bottom = 10.dp
                                                ).fillMaxWidth().clickable {
                                                (exerciseClick?.invoke(workoutExercise))
                                            }
                                        )
                                        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                                        workoutExercise.sets.forEach { set ->
                                            WorkoutSetRow(set) {
                                                setClick?.invoke(set)
                                            }
                                        }
                                    }
                                }

                                //}
                            }
                        }
                    }
                }
            },
        )
    }
}




