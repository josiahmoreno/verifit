package com.example.verifit.diary

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Preview
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
import com.example.verifit.sets.SampleStatsDataProvider
import com.example.verifit.sets.StatsRow


@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun GenericStatsWithButtons( @PreviewParameter(DialogDataProvider::class) state: DialogData,
                             view : (() -> Unit)? = null,
                             close : (() -> Unit)? = null
                             )
{
    Column {
        GenericStats(state = state)
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
            Button(onClick = { view?.invoke()}) {
                Icon(Icons.Filled.Preview, "Preview",tint = Color.White)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("View", color= Color.White)
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = { close?.invoke() }, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(Icons.Filled.Close, "Close",tint = Color.White)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Close", color= Color.White)
            }
        }
    }


}


@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun GenericStats( @PreviewParameter(DialogDataProvider::class) state: DialogData) {
    Column {
        Text(text = "${state.title}",
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        state.data.forEach {
            StatsRow(it.first, it.second, it.third)
        }
        Spacer(modifier = Modifier.padding(bottom = 30.dp))
    }
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun GenericStatsDialog(show: MutableState<Boolean> = mutableStateOf(true),@PreviewParameter(DialogDataProvider::class)state: DialogData,
                       view : (() -> Unit)? = null,
                       close : (() -> Unit)? = null
                       ) {
    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (show.value) {
        Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    show.value = false
                },

                content = {
                    Card(modifier = Modifier.padding(28.dp)) {
                        GenericStatsWithButtons(state, view, close)
                    }
                }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun GenericStatsWithButtonDialog(
                                 @PreviewParameter(DialogDataProvider::class) state: DialogData,
                                 dismissRequest: (() -> Unit)? = null,
                                 view : (() -> Unit)? = null,
                                 close : (() -> Unit)? = null
) {

        Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    dismissRequest?.invoke()
                },

                content = {
                    Card(modifier = Modifier.padding(28.dp)) {
                        GenericStatsWithButtons(state, view, close )
                    }
                }
        )
}


data class DialogData(
        val title: String,
        val data: List<Triple<String,String,String>>
)


class DialogDataProvider: PreviewParameterProvider<DialogData> {
    override val values = sequenceOf(
            DialogData("Saturday, Mar 12 200",
                    listOf(
                            Triple("Total Sets", "16", "sets"),
                            Triple("Total Reps", "36", "reps"),
                            Triple("Total Volume", "100.0", "kg"),
                            Triple("Total Exercises", "3", "exercises"),
                    )
            ),
    )
    override val count: Int = values.count()
}


