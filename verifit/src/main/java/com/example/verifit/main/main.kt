package com.example.verifit.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.*
import com.example.verifit.addexercise.composables.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewPagerScreen(
        //@PreviewParameter(SampleViewPagerDataProvider::class) fetchViewPagerDataResult: FetchViewPagerDataResult,
        viewModel: WorkoutDayViewPagerViewModel
){
    val state = viewModel.viewState.collectAsState()
    val pagerState = rememberPagerState(state.value.pageSelected)
    val context = LocalContext.current
    LaunchedEffect(key1 = "ViewPagerScreen", block = {

        viewModel.oneShotEvents
                .onEach {
                    when (it) {
                        is OneShotEvents.ScrollToPage -> pagerState.animateScrollToPage(it.pageSelected)
                        is OneShotEvents.GoToExercisesList -> {

                            val intent = Intent(context, ExercisesActivity::class.java)
                            MainActivity.date_selected = it.dateString
                            context.startActivity(intent)

                        }
                    }
                }
                .collect()
    })
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
                                    viewModel.onAction(UiAction.GoToTodayClicked)
                                }) {
                                    Icon(Icons.Filled.Today, "comment")
                                }
                            }
                    )
                },
                content = {
                    HorizontalPager(count = state.value.FetchViewPagerDataResult.workDays.count(), state = pagerState) { page ->
                        // ...page content
                        WorkoutDayScreen(data = state.value.FetchViewPagerDataResult.workDays[page],
                                workoutExerciseClick = {viewModel.onAction(UiAction.WorkoutExerciseClicked(it))},
                                dateCardClick = {viewModel.onAction(UiAction.DateCardClicked())},
                                setClick = {viewModel.onAction(UiAction.SetClicked(it))}
                                )
                    }
                }
        )
    }
}

@JvmInline
value class FetchViewPagerDataResult(
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
    dateCardClick: ((SingleViewPagerScreenData)-> Unit)? = null
) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier.clickable {
                //this opens up the exercise activity
                //date selected is now this
                dateCardClick?.invoke(data)
            },
                horizontalAlignment = Alignment.CenterHorizontally
                ){
                Text(data.day, fontSize = 26.sp, modifier = Modifier.padding(top = 10.dp))
                Text(data.date, modifier = Modifier.padding(bottom = 10.dp), color = Color.DarkGray)
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
                                modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .clickable {
                                            //this is where the exercise is clicked
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
    val day: String,
    val date: String
) {

}

// this is the color circle, the title of the exercise and the sets
@JvmInline
value class WorkoutExercisesViewData(val workoutExercisesWithColors:  List<Pair<WorkoutExercise,Color>> )

class SampleViewPagerDataProvider: PreviewParameterProvider<FetchViewPagerDataResult> {
    override val values = sequenceOf(
        FetchViewPagerDataResult(
                getSampleViewPagerData().toList()
        ),
    )
    override val count: Int = values.count()
}

@ExperimentalComposeUiApi
class Compose_MainActivity : AppCompatActivity() {
// Helper Data Structure
private val viewModel: WorkoutDayViewPagerViewModel by viewModels {
    MainViewPagerViewModelFactory(this)
}

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ViewPagerScreen(viewModel)
            }
        }
    }
}

class MainViewPagerViewModelFactory(
        val applicationContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutDayViewPagerViewModel(
                FetchViewPagerDataUseCase = FetchViewPagerDataUseCase(PrefWorkoutServiceImpl(applicationContext = applicationContext))
        ) as T
    }
}

