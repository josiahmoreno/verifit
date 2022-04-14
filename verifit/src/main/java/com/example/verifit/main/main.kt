package com.example.verifit.main

import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import com.example.verifit.*
import com.example.verifit.addexercise.composables.WorkoutSetRow
import com.example.verifit.sets.SetStatsDialog
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
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
    viewModel: WorkoutDayViewPagerViewModel,
){
    val state = viewModel.viewState.collectAsState()
    val pagerState = rememberPagerState(state.value.pageSelected)
    val context = LocalContext.current
    val showSetStatsDialog = remember { mutableStateOf(false) }
    val set = remember {
        mutableStateOf<WorkoutSet?>(null)
    }
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            -> {
                viewModel.onAction(UiAction.OnResume)
            }
            else -> Unit
        }
    }
    LaunchedEffect(key1 = "ViewPagerScreen", block = {

        viewModel.oneShotEvents
                .onEach {
                    when (it) {
                        is OneShotEvents.ScrollToPage -> pagerState.animateScrollToPage(it.pageSelected)
                        is OneShotEvents.GoToExercisesList -> {

                            val intent = Intent(context, ExercisesActivity::class.java)
                            DateSelectStore.date_selected = it.dateString
                            context.startActivity(intent)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                        is OneShotEvents.GoToAddExercise -> {
                            val `in` = Intent(context, Compose_AddExerciseActivity::class.java)
                            `in`.putExtra("exercise", it.exerciseName)

                            context.startActivity(`in`)
                            context.getActivity()?.overridePendingTransition(0, 0)
                        }
                        is OneShotEvents.ShowSetStats -> {
                            showSetStatsDialog.value = true
                            set.value = it.set
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
                                dateCardClick = { data ->
                                    viewModel.onAction(UiAction.DateCardClicked(data))
                                },
                                setClick = {viewModel.onAction(UiAction.SetClicked(it))}
                                )
                    }
                }
        )
        SetStatsDialog(showSetStatsDialog, set.value)
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@JvmInline
value class FetchViewPagerDataResult(
    val workDays: List<SingleViewPagerScreenData>,
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
    dateCardClick: ((SingleViewPagerScreenData) -> Unit)? = null,
) {
        val exercisesViewData = data.exercisesViewData.workoutExercisesWithColors.observeAsState(listOf())
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable {
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
                //exerciseviewdata changes here

                items(exercisesViewData.value) { workoutExercise ->
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


fun getSampleViewPagerData() : Sequence<SingleViewPagerScreenData> {
    val day1 = WorkoutDay(

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
    }
    val day2 = WorkoutDay(

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
    }
    return sequenceOf(
        SingleViewPagerScreenData(

            WorkoutExercisesViewData(
                MutableLiveData(day1.exercises.map { Pair(it, Color.Blue) })
            ),
        "Friday", "February 17 2022", day1
        ),
        SingleViewPagerScreenData(
            WorkoutExercisesViewData(
                MutableLiveData(day2.exercises.map { Pair(it, Color.Red) })
            ), "Saturday", "February 18 2022", day2
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
    val date: String,
    val workoutDay: WorkoutDay,
) {

}

// this is the color circle, the title of the exercise and the sets
@JvmInline
value class WorkoutExercisesViewData(val workoutExercisesWithColors: LiveData<List<Pair<WorkoutExercise, Color>>>)

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
    MainViewPagerViewModelFactory(this, WorkoutServiceSingleton.getWorkoutService(applicationContext), KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
}

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                ViewPagerScreen(viewModel)
            }
        }
    }
}

class MainViewPagerViewModelFactory(
    val applicationContext: Context,
    val workoutService: WorkoutService,
    val knownExerciseService: KnownExerciseService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutDayViewPagerViewModel(
                FetchViewPagerDataUseCase = FetchViewPagerDataUseCase(
                    workoutService = workoutService, knownExerciseService = knownExerciseService
                )
        ) as T
    }
}

