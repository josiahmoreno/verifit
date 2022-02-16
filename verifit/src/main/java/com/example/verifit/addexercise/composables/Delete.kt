package com.example.verifit.addexercise.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun Delete(  yes: (() -> Unit)? = null,
             no: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxWidth() ) {
        Text(text = "Delete selected set?",
            color = MaterialTheme.colors.primary,
            fontSize = 22.sp,
            modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
        ) {
            Button(
                onClick = {
                    yes?.invoke()
                },
                modifier = Modifier
                    .width(84.dp)
                    .padding(end = 10.dp, top = 10.dp, bottom = 10.dp)
                    .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
            ) {
                Text("Yes")
            }
            OutlinedButton(
                onClick = {

                    no?.invoke()
                },
                border = BorderStroke(1.dp, MaterialTheme.colors.primary),

                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                modifier = Modifier
                    .width(84.dp)
                    .padding(start = 10.dp,top = 10.dp, bottom = 10.dp )
            ) {
                Text("No")
            }
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun DeleteDialog(show: MutableState<Boolean> = mutableStateOf(true), yes: (() -> Unit)? = null,
                  no: (() -> Unit)? = null) {
    if (show.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                show.value = false
            },

            content = {
                Card(modifier = Modifier.padding(28.dp)) {
                    Delete({

                        yes?.invoke()
                        show.value = false
                    }, {
                        no?.invoke()
                        show.value = false
                    })
                }

            }
        )
    }
}



