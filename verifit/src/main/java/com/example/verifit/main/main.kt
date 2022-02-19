package com.example.verifit.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verifit.*
import com.example.verifit.addexercise.composables.WorkoutSetRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ViewPagerScreen(@PreviewParameter(SampleViewPagerDataProvider::class) viewPagerData: ViewPagerData){
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

                    HorizontalPager(count = viewPagerData.workDays.count(), state = pagerState) { page ->
                        // ...page content
                        WorkoutDayScreen(data = viewPagerData.workDays[page])
                    }
                }
        )
    }
}

@JvmInline
value class ViewPagerData(
    val workDays: List<SingleViewPagerScreenData>
)


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun WorkoutDayScreen(
    @PreviewParameter(SampleWorkoutDayScreenDataProvider::class) data: SingleViewPagerScreenData,
    workoutExerciseClick: ((WorkoutExercise) -> Unit)? = null,
    setClick: ((WorkoutSet) -> Unit)? = null,
) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier.clickable {  },
                horizontalAlignment = Alignment.CenterHorizontally
                ){
                Text(data.Day, fontSize = 26.sp, modifier = Modifier.padding(top = 10.dp))
                Text(data.Date, modifier = Modifier.padding(bottom = 10.dp), color = Color.DarkGray)
            }

            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color.LightGray)
            ) {

                items(data.exercisesViewData.workoutExercisesWithColors) { workoutExercise ->
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Card(elevation = 4.dp, modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                        Column {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentHeight().fillMaxWidth().clickable {
                                    (workoutExerciseClick?.invoke(workoutExercise.first))
                                },
                            ){
                                Spacer(modifier = Modifier
                                    .width(10.dp)
                                    .height(60.dp)
                                    .clip(RectangleShape))
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(workoutExercise.second)
                                ){

                                }
                                Spacer(modifier = Modifier
                                    .width(10.dp)
                                    .height(60.dp)
                                    .clip(RectangleShape))
                                Row(modifier = Modifier
                                    .height(60.dp)
                                    )
                                {
                                    Text(workoutExercise.first.exercise,
                                        maxLines = 1,
                                        fontSize = 26.sp,
                                        //textAlign = TextAlign.Center,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(top = 10.dp)


                                    )
                                }

                            }

                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            workoutExercise.first.sets.forEach { set ->
                                WorkoutSetRow(set) {
                                    setClick?.invoke(set)
                                }
                            }
                        }
                    }
                }
            }
        }
}

// Simple
fun GetCategoryIconTint(exercise_name: String?) : Int {
    val exercise_category = MainActivity.getExerciseCategory(exercise_name)
    when (exercise_category) {
        "Shoulders" -> {
            return android.graphics.Color.argb(255,
                0,
                116,
                189) // Primary Color
        }
        "Back" -> {
            return android.graphics.Color.argb(255, 40, 176, 192)
        }
        "Chest" -> {
            return android.graphics.Color.argb(255, 92, 88, 157)
        }
        "Biceps" -> {
            return android.graphics.Color.argb(255, 255, 50, 50)
        }
        "Triceps" -> {
            return android.graphics.Color.argb(255, 204, 154, 0)
        }
        "Legs" -> {
            return android.graphics.Color.argb(255, 212, 25, 97)
        }
        "Abs" -> {
            return android.graphics.Color.argb(255, 255, 153, 171)
        }
        else -> {
            return android.graphics.Color.argb(255, 52, 58, 64) // Grey AF
        }
    }
}

fun getSampleViewPagerData() : Sequence<SingleViewPagerScreenData> {
    return sequenceOf(
        SingleViewPagerScreenData(
        WorkoutExercisesViewData(
            WorkoutDay(

            ).apply {
                sets = arrayListOf(
                    WorkoutSet("1111", "Seated Machine Shoulder Press", "", 1.0, 111.0),
                    WorkoutSet("1222", "Seated Machine Shoulder Press", "", 1.1, 122.0),
                    WorkoutSet("1222", "Seated Machine Shoulder Press", "", 1.2, 133.0),
                    WorkoutSet("1222", "Seated Machine Shoulder Press", "", 1.2, 144.0),
                    WorkoutSet("1222",
                        "Flat Barbell Pump",
                        "",
                        1.2,
                        144.0)
                )
                UpdateData()
            }.exercises.map { Pair(it, Color.Blue) }
        ),
        "Friday", "February 17 2022"),
    SingleViewPagerScreenData(
        WorkoutExercisesViewData(
            WorkoutDay(

            ).apply {
                sets = arrayListOf(
                    WorkoutSet("1111", "Chin Downward Dog", "", 1.0, 111.0),
                    WorkoutSet("1111", "Chin Downward Dog", "", 2.0, 222.0),
                    WorkoutSet("1222", "Seated Leg Hump", "", 1.1, 122.0),
                    WorkoutSet("1222",
                        "Inclined Barbell Pump",
                        "",
                        1.2,
                        144.0)
                )
                UpdateData()
            }.exercises.map { Pair(it, Color.Red) }
        ), "Saturday", "February 18 2022"
    )
    )

}
class SampleWorkoutDayScreenDataProvider : PreviewParameterProvider<SingleViewPagerScreenData> {
    override val values = getSampleViewPagerData()

    override val count: Int = values.count()
}

data class SingleViewPagerScreenData(
    val exercisesViewData: WorkoutExercisesViewData,
    val Day: String,
    val Date: String
) {

}

@JvmInline
value class WorkoutExercisesViewData(val workoutExercisesWithColors:  List<Pair<WorkoutExercise,Color>> ) {

}

class SampleViewPagerDataProvider: PreviewParameterProvider<ViewPagerData> {
    override val values = sequenceOf(
        ViewPagerData(
                getSampleViewPagerData().toList()
        ),
    )
    override val count: Int = values.count()
}

