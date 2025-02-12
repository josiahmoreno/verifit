package com.example.verifit.addexercise.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.comment.CommentViewModel
import com.example.verifit.comment.UiAction

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun CommentContent( @PreviewParameter(SampleCommentDataProvider::class) state: CommentData, save: ((String) -> Unit)? = null,
             clear: ((String) -> Unit)? = null) {
    val comment = remember{ mutableStateOf(state.comment)}
    Column(modifier = Modifier.fillMaxWidth() ) {
        Text(text = "Add Comment",
            color = MaterialTheme.colors.primary,
            fontSize = 22.sp,
            modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        TextField(
            value = comment.value,
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 16.sp),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            onValueChange = {
                comment.value = it
            },
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
        ) {
            Button(
                onClick = {
                    save?.invoke(comment.value)
                },
                modifier = Modifier
                        .width(90.dp)
                        .padding(end = 10.dp, top = 10.dp, bottom = 10.dp)
                        .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = {
                    comment.value = ""
                    clear?.invoke(comment.value)
                },
                border = BorderStroke(1.dp, MaterialTheme.colors.primary),

                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                modifier = Modifier
                        .width(90.dp)
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Text("Clear")
            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun CommentContentHilt()
{
    CommentContent(hiltViewModel())
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun CommentContent(navHostController: NavHostController,exerciseName: String?, date: String?, comment: String?)
{
  //  CommentContent(CommentViewModel(navHostController = navHostController,exerciseName!!, date!! ,WorkoutServiceSingleton.getWorkoutService(
    //    LocalContext.current),comment))
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun CommentContent(viewModel: CommentViewModel)
 {
    val state = viewModel.viewState.collectAsState()
    Card(modifier = Modifier.padding(28.dp)) {
        CommentContent(state = CommentData(state.value.comment), save = {
            viewModel.onAction(UiAction.SaveAction(it))
        }, clear = {
            viewModel.onAction(UiAction.ClearAction(it))
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun CommentDialog(show: MutableState<Boolean> = mutableStateOf(true), @PreviewParameter(SampleCommentDataProvider::class) state: CommentData, save: ((String) -> Unit)? = null,
                  clear: ((String) -> Unit)? = null
) {
    if (show.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                show.value = false
            },

            content = {
                Card(modifier = Modifier.padding(28.dp)) {
                    CommentContent(state,save, clear)
                }

            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun CommentDialog(show: MutableState<Boolean> = mutableStateOf(true),  comment: String?, save: ((String) -> Unit)? = null,
                  clear: ((String) -> Unit)? = null
                  ) {
        CommentDialog(show,
            CommentData(
                comment = comment ?: ""
            )
        , save, clear)
}

data class CommentData(
    val comment: String,
)


class SampleCommentDataProvider: PreviewParameterProvider<CommentData> {
    override val values = sequenceOf(
        CommentData(
            "Hit the Max Rep!",
        ),
    )
    override val count: Int = values.count()
}


