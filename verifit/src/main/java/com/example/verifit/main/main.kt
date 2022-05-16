package com.example.verifit.main

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import com.example.verifit.*
import com.example.verifit.R
import com.example.verifit.addexercise.composables.WorkoutSetRow
import com.example.verifit.common.*
import com.example.verifit.me.TopAppBarDropdownMenu
import com.example.verifit.settings.Compose_SettingsActivity
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import java.lang.Exception


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewPagerScreenHilt() {
    val viewModel: ViewPagerViewModel = hiltViewModel()
    ViewPagerScreen(viewModel = viewModel)
}


var addClick: (() -> Unit)? = null

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun ViewPagerScreen(
    //@PreviewParameter(SampleViewPagerDataProvider::class) fetchViewPagerDataResult: FetchViewPagerDataResult,
    viewModel: ViewPagerViewModel,
) {

    val state = viewModel.viewState.collectAsState()

    val pagerState: PagerState = rememberPagerState(state.value.pageSelected)

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { page ->
            Log.d("ViewPagerScreen.Compose", "snapshotFlow $page")
            addClick = {
                Log.d("ViewPagerScreen.Compose", "wtf")
                viewModel.onAction(UiAction.StartNewExerciseClicked(state.value.FetchViewPagerDataResult.workDays[page].workoutDay))
            }
            Log.d("ViewPagerScreen.Compose", "snapshotFlow2 $addClick")
        }
    }
    LaunchedEffect(key1 = "ViewPagerScreen", block = {

        viewModel.oneShotEvents
            .onEach {
                when (it) {
                    is OneShotEvents.ScrollToPage -> pagerState?.animateScrollToPage(it.pageSelected)
                }
            }.collect()
    })

    val bodyContent = remember { mutableStateOf("Select menu to change content") }
    Scaffold(


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
                        viewModel.onAction(UiAction.NavigateToCalendar(pagerState.currentPage))
                    }) {
                        Icon(Icons.Filled.CalendarViewMonth, "comment")
                    }
                    IconButton(onClick = {
                        Log.d("ViewPagerScreen.Compose", "addClick $addClick")
                        addClick?.invoke()


                    }) {
                        Icon(Icons.Filled.Add, "comment", modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = {
                        viewModel.onAction(UiAction.GoToTodayClicked)
                    }) {
                        Icon(Icons.Filled.Today, "comment")
                    }
                    TopAppBarDropdownMenu(listOf("Settings"), bodyContent) {
                        when (it) {
                            "Settings" -> {
                                viewModel.onAction(UiAction.GoToSettings)
                            }
                        }
                    }

                }
            )
        },
        content = { padding ->

            Log.d("Main", "scaffold.content0")
            if (!state.value.loading) {
                Log.d("Main", "scaffold.content1")


                Log.d("Main", "scaffold.content2")
                HorizontalPager(count = state.value.FetchViewPagerDataResult.workDays.count(),
                    state = pagerState!!) { page ->
                    Log.d("ViewPagerCompose", "observing $page..")
                    val exercisesViewData =
                        state.value.FetchViewPagerDataResult.workDays[page].exercisesViewData.workoutExercisesWithColors.observeAsState()
                    if (exercisesViewData.value != null) {

                        Log.d("ViewPagerCompose",
                            "exercisesViewData size ${exercisesViewData.value}..")
                        WorkoutDayScreen(data = state.value.FetchViewPagerDataResult.workDays[page],
                            workoutExerciseClick = {
                                viewModel.onAction(UiAction.WorkoutExerciseClicked(it))
                            },
                            dateCardClick = { data ->
                                viewModel.onAction(UiAction.DateCardClicked(data))
                            },
                            setClick = { viewModel.onAction(UiAction.SetClicked(it)) },
                            startNewExerciseClick = {
                                viewModel.onAction(UiAction.StartNewExerciseClicked(it))
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            //BottomNavigationComposable(BottomNavItem.Home)
        }
    )
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
    startNewExerciseClick: ((WorkoutDay) -> Unit)? = null,
    backgroundColor : Color = colorResource(R.color.core_grey_05)
) {


    val exercisesViewData =
        data.exercisesViewData.workoutExercisesWithColors.observeAsState(data.exercisesViewData.workoutExercisesWithColors.value!!)
    Log.d("ViewPagerCompose",
        "WorkoutDayScreen starting.. ${data.date} exercisesViewData.value.size = ${exercisesViewData.value.size}")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //this opens up the exercise activity
                //date selected is now this
                dateCardClick?.invoke(data)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(data.day, fontSize = 26.sp, modifier = Modifier.padding(top = 10.dp))
            Text(data.date, modifier = Modifier.padding(bottom = 10.dp), color = Color.DarkGray)
        }

        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)



        if (exercisesViewData.value.isNotEmpty()) {
            ExercisesList(data.exercisesViewData, workoutExerciseClick, setClick)
        } else {
            Log.d("ViewPagerCompose.WorkoutDayScreen", "${data.date} SHOWWWW workout log empty")
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(backgroundColor)) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    //.background(Color.Green)
                    //.fillMaxHeight()

                    .weight(1.0f)) {
                    Text("Workout Log Empty",
                        Modifier
                            .align(Alignment.Center)
                            .alpha(.6f),
                        fontSize = 22.sp)
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        startNewExerciseClick?.invoke(data.workoutDay)
                    }) {
                    Icon(Icons.Filled.Add, null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(48.dp))
                    Text("Start New Exercise", modifier = Modifier
                        .alpha(.6f)
                        .padding(top = 6.dp)
                        .align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }

        }


    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun ExercisesList(
    @PreviewParameter(WorkoutExercisesViewDataProvider::class) data: WorkoutExercisesViewData,
    workoutExerciseClick: ((WorkoutExercise) -> Unit)? = null,
    setClick: ((WorkoutSet) -> Unit)? = null,
    backgroundColor : Color = colorResource(R.color.core_grey_05)
) {
    //val data = getSampleViewPagerData().first().exercisesViewData

    val exercisesViewData =
        data.workoutExercisesWithColors.observeAsState(data.workoutExercisesWithColors.value!!)
    if (exercisesViewData.value.isEmpty()) throw Exception()
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        //exerciseviewdata changes here


        items(exercisesViewData.value) { workoutExercise ->
            val (exerciseLiveDataParent, color) = workoutExercise
            val workoutExerciseState =
                exerciseLiveDataParent.exerciseLiveData.observeAsState(workoutExercise.first.exerciseLiveData.value!!)
            if (workoutExerciseState.value.exercise == "WTF") {
                throw Exception("bruh how")
            }
            if (workoutExerciseState.value.isNull) {
                Text("should of been deleted")
                return@items
            }
            val hmm = workoutExerciseState.value.exercise
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
                                (workoutExerciseClick?.invoke(workoutExerciseState.value))
                            },
                    ) {
                        Spacer(modifier = Modifier
                            .width(10.dp)
                            .height(60.dp)
                            .clip(RectangleShape))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color)
                        ) {

                        }
                        Spacer(modifier = Modifier
                            .width(10.dp)
                            .height(60.dp)
                            .clip(RectangleShape))
                        Row(modifier = Modifier
                            .height(60.dp)
                        )
                        {
                            Text(workoutExerciseState.value.exercise,
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
                    //eat of the sets of the workout exercise
                    workoutExerciseState.value.sets.forEach { set ->
                        WorkoutSetRow(workoutSet = set, click = {
                            setClick?.invoke(set)
                        })
                    }
//                    Row(horizontalArrangement = Arrangement.Center) {
//                        TextButton(content = {Text("Add to Exercise")}, modifier = Modifier.padding(start = 10.dp), onClick = {
//
//                        })
//                        TextButton(content = {Text("Show Stats")}, modifier = Modifier.padding(start = 10.dp), onClick = {
//
//                        })
//                    }

                }
            }
        }
    }
}

class WorkoutExercisesViewDataProvider : PreviewParameterProvider<WorkoutExercisesViewData> {
    override val values = sequenceOf(getSampleViewPagerData().first().exercisesViewData)

    override val count: Int = values.count()
}


fun getSampleViewPagerData(): Sequence<SingleViewPagerScreenData> {
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
            WorkoutSet("1111", "Chin Downward Dog2", "", 1.0, 111.0),
            WorkoutSet("1111", "Chin Downward Dog", "", 2.0, 222.0),
            WorkoutSet("1222", "Seated Leg Hump2", "", 1.1, 122.0),
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
                MutableLiveData(day1.exercises.map {
                    Pair(ExerciseLiveData(MutableLiveData(it)),
                        Color.Blue)
                })
            ),
            "Friday", "February 17 2022", day1
        ),
        SingleViewPagerScreenData(
            WorkoutExercisesViewData(
                MutableLiveData(day2.exercises.map {
                    Pair(ExerciseLiveData(MutableLiveData(it)),
                        Color.Red)
                })
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
value class WorkoutExercisesViewData(val workoutExercisesWithColors: LiveData<List<Pair<ExerciseLiveData, Color>>>)

@JvmInline
value class ExerciseLiveData(val exerciseLiveData: LiveData<WorkoutExercise>)


class SampleViewPagerDataProvider : PreviewParameterProvider<FetchViewPagerDataResult> {
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
    private val viewModel: ViewPagerViewModel by viewModels {
        Log.d("MainViewModel", "yo00")
        MainViewPagerViewModelFactory(applicationContext = this,
            workoutService = WorkoutServiceSingleton.getWorkoutService(applicationContext),
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(
                applicationContext),
            NoOpNavigateToAddExerciseUseCase()
        )

    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainViewModel", "yo001")
        setContent {
            AppCompatTheme {
                //val view = viewModel
                ViewPagerScreen(viewModel)
                //  ViewPagerScreen2(viewModel)
            }
        }
        Log.d("MainViewModel", "yo002")
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewPagerScreen2(
    viewModel: ViewPagerViewModel,
    //@PreviewParameter(SampleViewPagerDataProvider::class) fetchViewPagerDataResult: FetchViewPagerDataResult,

) {
    val state = viewModel.viewState.collectAsState()
    Scaffold(


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
        },
        bottomBar = {

        }
    )
}

class MainViewPagerViewModelFactory(
    val applicationContext: Context,
    val workoutService: WorkoutService,
    val knownExerciseService: KnownExerciseService,
    val goToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val date: SavedStateHandle? = null,
    val NavigateToCalendarUseCase: NavigateToCalendarUseCase = NoOpNavigateToCalendarUseCase(),
    val NavigateToExercisesListUseCase: NavigateToExercisesListUseCase = MockNavigateToExercisesListUseCase(),
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewPagerViewModel(
            FetchViewPagerDataUseCase = FetchViewPagerDataUseCase(
                workoutService = workoutService, colorGetter = ColorGetterImpl(knownExerciseService)
            ), goToAddExerciseUseCase, NavigateToExercisesListUseCase =
            NavigateToExercisesListUseCase,
            NavigateToCalendarUseCase = NavigateToCalendarUseCase

        ) as T
    }
}

