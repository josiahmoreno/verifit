package com.example.verifit.sets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.verifit.WorkoutSet

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun SetStats( @PreviewParameter(SampleSetStatsDataProvider::class) state: SetStatsData) {
    Column {
        Row{
            Text(text = "${state.weight}",
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)
            )
            Text(text = "kg",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 28.dp, start = 5.dp)
            )
            Text(text = "${state.reps}",
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 20.dp, start = 20.dp)
            )
            Text(text = "reps",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 28.dp, start = 5.dp)
            )
        }

        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        StatsRow("Volume", state.volume, "kg")
        StatsRow("Estimated 1RM",state.estimated1RM, "kg")
        Spacer(modifier = Modifier.padding(bottom = 30.dp))
    }
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun SetStatsDialog(show: MutableState<Boolean> = mutableStateOf(true), @PreviewParameter(SampleSetStatsDataProvider::class) state: SetStatsData) {
    if (show.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                show.value = false
            },

            content = {
                Card(modifier = Modifier.padding(28.dp)) {
                    SetStats(state)
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun SetStatsDialog(show: MutableState<Boolean> = mutableStateOf(true),  workoutSet: WorkoutSet?) {
    if(workoutSet != null){
        SetStatsDialog(show,
            SetStatsData(
                workoutSet.weight.toString(),
                workoutSet.reps.toString(),
                workoutSet.volume.toString(),
                workoutSet.eplayOneRepMax.toString()
            )
        )
    }
}

data class SetStatsData(
    val weight: String,
    val reps: String,
    val volume:String,
    val estimated1RM: String
)


class SampleSetStatsDataProvider: PreviewParameterProvider<SetStatsData> {
    override val values = sequenceOf(
        SetStatsData("80",
            "100",
            "400",
            "100",
        ),
    )
    override val count: Int = values.count()
}


