package com.example.verifit.addexercise.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.verifit.MviPreviewViewStateProvider
import com.example.verifit.WorkoutExercise
import com.example.verifit.WorkoutSet
import com.github.mikephil.charting.data.LineData

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun Graph( @PreviewParameter(SampleGraphDataProvider::class) state: GraphData) {
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
        }, update = { view ->
            view.data = state.lineData;
        }, modifier = Modifier.fillMaxWidth().height(500.dp).padding(bottom = 20.dp, top = 20.dp)
        )
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
                Card(modifier = Modifier.padding(28.dp)) {
                    Graph(state)
                }

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


