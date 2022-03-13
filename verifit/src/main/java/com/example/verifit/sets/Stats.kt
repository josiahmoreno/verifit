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
import com.example.verifit.WorkoutExercise

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun Stats( @PreviewParameter(SampleStatsDataProvider::class) state: StatsData) {
    Column {
        Text(text = "${state.exerciseName}",
            color = MaterialTheme.colors.primary,
            fontSize = 22.sp,
            modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        StatsRow("Total Sets", state.totalSets, "sets")
        StatsRow("Total Reps", state.totalReps ,"reps")
        StatsRow("Total Volume", state.totalVolume, "kg")
        StatsRow("Max Weight", state.maxWeight, "kg")
        StatsRow("Max Reps",state.maxReps, "reps")
        StatsRow("Max Set Volume",state.maxSetVolume, "kg")
        StatsRow("Estimated 1RM",state.estimated1RM, "kg")
        Spacer(modifier = Modifier.padding(bottom = 30.dp))
    }
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun StatsDialog(show: MutableState<Boolean> = mutableStateOf(true), @PreviewParameter(SampleStatsDataProvider::class) state: StatsData) {
    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (show.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                show.value = false
            },

            content = {
                Card(modifier = Modifier.padding(28.dp)) {
                    Stats(state)
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun StatsDialog(show: MutableState<Boolean> = mutableStateOf(true),  workoutExercise: WorkoutExercise?) {
    if(workoutExercise != null){
        StatsDialog(show,
            StatsData(
                workoutExercise.exercise,
                workoutExercise.totalSets.toString(),
                workoutExercise.totalReps.toString(),
                workoutExercise.volume.toString(),
                workoutExercise.maxWeight.toString(),
                workoutExercise.maxWeight.toString(),
                workoutExercise.maxSetVolume.toString(),
                workoutExercise.estimatedOneRepMax.toString()
            )
        )
    }
}

data class StatsData(
    val exerciseName: String,
    val totalSets: String,
    val totalReps:String,
    val totalVolume: String,
    val maxWeight: String,
    val maxReps: String,
    val maxSetVolume: String,
    val estimated1RM: String
)

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun StatsRow(exerciseName: String = "Flat Barbell Bench Press",
            amount : String = "1234",
             unit: String = "sets"
             ){
    Column {
        Text(text = exerciseName,
            color = Color.Black,
            modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)

        )
        Row(modifier = Modifier.padding(start = 20.dp)) {
            Text(text = amount,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                fontSize = 12.sp
            )
            Text(text = unit,
                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                fontSize = 12.sp
            )
        }
    }
}

class SampleStatsDataProvider: PreviewParameterProvider<StatsData> {
    override val values = sequenceOf(
        StatsData("Flat Barbell Bench Press",
            "4",
            "210",
            "840.0",
            "25",
            "9",
            "720",
            "50"
        ),
    )
    override val count: Int = values.count()
}


