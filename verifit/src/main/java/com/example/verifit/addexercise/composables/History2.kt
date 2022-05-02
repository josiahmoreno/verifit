
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.verifit.addexercise.composables.AddExerciseViewState
import com.example.verifit.addexercise.composables.WorkoutSetRow
import com.example.verifit.addexercise.history.FetchHistoryUseCase
import com.example.verifit.addexercise.history.HistoryViewModel
import com.example.verifit.addexercise.history.UiAction
import com.example.verifit.charts.ChartsViewModel
import com.example.verifit.charts.FetchChartsDataUseCaseImpl
import com.example.verifit.common.ShowExerciseStatsUseCase
import com.example.verifit.common.ShowSetStatsUseCase
import com.example.verifit.workoutservice.WorkoutService
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
                HistoryContent(content = list.value,
                    exerciseClick = exerciseClick,
                    setClick = setClick,
                    exerciseName = title)
            },
        )
    }
}

class HistoryViewModelFactory(
    val exerciseName: String,
    val workoutService: WorkoutService
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(
            exerciseName,
            FetchHistoryUseCase = FetchHistoryUseCase(workoutService),
            ShowExerciseStatsUseCase = ShowExerciseStatsUseCase(),
            ShowSetStatsUseCase = ShowSetStatsUseCase()
        )
                as T
    }
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview
@Composable
fun HistoryContent(exerciseName: String?) {
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(
            exerciseName = exerciseName!!,
            workoutService = WorkoutServiceSingleton.getWorkoutService(LocalContext.current)
        )
    )
    HistoryContent(viewModel)
}


@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview
@Composable
fun HistoryContent(viewModel: HistoryViewModel){
    val state by viewModel.viewState.collectAsState()
    HistoryContent(content = state.data,
        exerciseClick = {  viewModel.onAction(UiAction.ExerciseClick)},
        setClick = {  viewModel.onAction(UiAction.SetClick)},
        exerciseName = state.exerciseName)
}
@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview
@Composable
fun HistoryContent(content : List<WorkoutExercise>,
                   exerciseClick: ((WorkoutExercise) -> Unit)? = null,
                   setClick: ((WorkoutSet) -> Unit)? = null,
                   exerciseName: String? = "null"){
    val list : State<List<WorkoutExercise>> = remember{mutableStateOf(content)}
    Card(modifier = Modifier.padding(28.dp)) {
        Column {


            Text(text = "${exerciseName}",
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
                                modifier = Modifier
                                    .padding(start = 15.dp,
                                        end = 15.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    )
                                    .fillMaxWidth()
                                    .clickable {
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
}




