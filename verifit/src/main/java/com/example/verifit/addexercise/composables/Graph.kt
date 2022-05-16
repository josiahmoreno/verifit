package com.example.verifit.addexercise.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.addexercise.history.exerciseName
import com.example.verifit.charts.FetchChartsDataUseCaseImpl
import com.example.verifit.common.FetchGraphDialogDataUseCase
import com.example.verifit.common.FetchGraphDialogDataUseCaseImpl
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun GraphContentHilt() {

    val viewModel :GraphContentViewModel = hiltViewModel()
    GraphContent(GraphData(viewModel.lineData))
}

@HiltViewModel
class GraphContentViewModel @Inject constructor( val saveStateHandle: SavedStateHandle,val FetchGraphDialogDataUseCase: FetchGraphDialogDataUseCase): ViewModel(){

    var lineData: LineData = FetchGraphDialogDataUseCase(saveStateHandle.exerciseName ?: "")
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun GraphContent(exerciseName: String?) {
    val useCase = FetchGraphDialogDataUseCaseImpl(workoutService = WorkoutServiceSingleton.getWorkoutService(
        LocalContext.current))
    val data = useCase(exerciseName?: "")
    GraphContent(GraphData(data))
}

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun GraphContent(@PreviewParameter(SampleGraphDataProvider::class) state: GraphData) {
    Card(modifier = Modifier.padding(28.dp)) {
        Column {
            Text(text = "Graph",
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier.padding(all = 20.dp)
            )
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
            AndroidView(factory = { context ->
                com.github.mikephil.charting.charts.LineChart(context).apply {
                    data = state.lineData
                    description.isEnabled = false
                }
            },
                update = { view ->
                    view.data = state.lineData;
                },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(bottom = 20.dp, top = 20.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun GraphDialog(show: MutableState<Boolean> = mutableStateOf(true), @PreviewParameter(SampleGraphDataProvider::class) state: GraphData) {
    if (show.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                show.value = false
            },

            content = {
                    GraphContent(state)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun GraphDialog(show: MutableState<Boolean> = mutableStateOf(true),  lineData: LineData?) {
    if(lineData != null){
        GraphDialog(show,
            GraphData(
                lineData
            )
        )
    }
}

data class GraphData(
    val lineData: LineData,
)


class SampleGraphDataProvider: PreviewParameterProvider<GraphData> {
    override val values = sequenceOf(
        GraphData(
            LineData(),
        ),
    )
    override val count: Int = values.count()
}


