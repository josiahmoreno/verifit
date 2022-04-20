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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.verifit.sets.StatsRow


@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun GenericStatsWithButtons(@PreviewParameter(PreviewDialogDataProvider::class) state: DialogData,
                            left : (() -> Unit)? = null,
                            close : (() -> Unit)? = null,
                            leftImageVector: ImageVector? = null,
                            leftTitle: String? = null
                             )
{
    Column {
        GenericStats(state = state)
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
            Button(onClick = { left?.invoke()}) {
                Icon(leftImageVector ?: Icons.Filled.Preview, "Preview",tint = Color.White)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(leftTitle ?: "View", color= Color.White)
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
fun GenericStats( @PreviewParameter(PreviewDialogDataProvider::class) state: DialogData) {
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
fun GenericStatsDialog(show: MutableState<Boolean> = mutableStateOf(true), @PreviewParameter(PreviewDialogDataProvider::class)state: DialogData,
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
        @PreviewParameter(PreviewDialogDataProvider::class) state: DialogData,
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

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun <T: DialogDataProvider> GenericStatsWithButtonDialog( show: MutableState<T?> ,
        view : (() -> Unit)? = null,
        close : (() -> Unit)? = null,
      leftImageVector: ImageVector? = null,
      leftTitle: String? = null
) {

    show.value?.let { dialogData ->
        Dialog(
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    onDismissRequest = {
                        show.value = null
                    },

                    content = {
                        Card(modifier = Modifier.padding(28.dp)) {
                                GenericStatsWithButtons(state = dialogData.dialogData,
                                        left = view,
                                        close = { show.value = null },
                                        leftImageVector = leftImageVector,
                                        leftTitle = leftTitle)
                        }
                    }
            )
    }

}


interface  DialogData {
    val title: String
    val data: List<Triple<String, String, String>>
}

data class DialogDataImpl(
        override val title: String,
        override val data: List<Triple<String,String,String>>,
        val diaryEntry: DiaryEntry
): DialogData
data class DialogDataViewOnly(
        override val title: String,
        override val data: List<Triple<String,String,String>>
): DialogData


interface DialogDataProvider{
     val dialogData : DialogData
}




class PreviewDialogDataProvider: PreviewParameterProvider<DialogData> {
    override val values = sequenceOf(
            DialogDataViewOnly("Saturday, Mar 12 200",
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


