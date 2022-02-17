package com.example.verifit.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verifit.*
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.example.verifit.addexercise.composables.*
import com.github.mikephil.charting.data.LineData
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ViewPagerScreen(){
    MaterialTheme() {

        Scaffold(
                drawerContent = { /*...*/ },
                topBar = {
                    TopAppBar(
                            backgroundColor = MaterialTheme.colors.primary,
                            title = {

                                Text(text = "Verifit",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis) // titl
                            },
                            actions = {
                                IconButton(onClick = {
                                    //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                }) {
                                    Icon(Icons.Filled.Today, "comment")
                                }
                            }
                    )
                },
                content = {
                    val pagerState = rememberPagerState()

                    HorizontalPager(count = 10, state = pagerState) { page ->
                        // ...page content
                        page + 2;
                    }
                }
        )
    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun WorkoutDayScreen(@PreviewParameter(SampleWorkoutDataProvider::class)workoutDay: WorkoutDay) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Friday", fontSize = 26.sp, modifier = Modifier.padding(top = 10.dp))
            Text("August 21 2022", modifier = Modifier.padding(bottom = 10.dp))
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        }

}


class SampleWorkoutDataProvider: PreviewParameterProvider<WorkoutDay> {
    override val values = sequenceOf(
            WorkoutDay(

            ).apply {
                sets = arrayListOf(
                        WorkoutSet("1111", "mememe", "", 1.0, 111.0),
                        WorkoutSet("1222", "mememe", "", 1.1, 122.0),
                        WorkoutSet("1222", "mememe", "", 1.2, 133.0),
                        WorkoutSet("1222", "mememe", "", 1.2, 144.0)
                )
            },
    )
    override val count: Int = values.count()
}

