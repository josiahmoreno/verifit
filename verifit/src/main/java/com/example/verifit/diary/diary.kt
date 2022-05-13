package com.example.verifit.diary

import android.content.Context
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
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.verifit.*
import com.example.verifit.R
import com.example.verifit.common.*
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalComposeUiApi
class Compose_DiaryActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DiaryViewModel by viewModels {
        //DiaryViewModelFactory(WorkoutServiceSingleton.getWorkoutService(context = applicationContext),
            //KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
        MockDiaryViewModelFactory2(applicationContext,
            KnownExerciseServiceSingleton.getKnownExerciseService(applicationContext),
            NoOpNavigateToAddExerciseUseCase(),
            NoOpNavigateToCommentUseCase(),
            MockNavigateToDiaryDayUseCase(),
            null)
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                DiaryListScreen(viewModel)
                Log.d("Diary","SetContent Finished")
            }
        }
    }
}
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiaryListScreenHilt() {
    val viewModel: DiaryViewModel = hiltViewModel()
    DiaryListScreen(viewModel)
}
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiaryListScreen(navigateTo: ((String)->Unit) , navController: NavHostController, date: String?, root: String) {
    val context = LocalContext.current
    val viewModel: DiaryViewModel = viewModel (factory =
        //DiaryViewModelFactory(WorkoutServiceSingleton.getWorkoutService(context = applicationContext),
        //KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
        MockDiaryViewModelFactory2(context = context,
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(context),
            GoToAddExerciseUseCase = NavigateToAddExerciseUseCaseImpl(navController),
            NavigateToCommentUseCase = NavigateToCommentUseCaseImpl( navigatorController = navController),
            NavigateToDiaryDayUseCase = NavigateToDiaryDayUseCaseImpl(navHostController = navController),
           savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ,
            NavigateToExerciseEntryStatsUseCase = NavigateToExerciseEntryStatsUseCaseImpl(navigatorController = navController)
            )
    )
    DiaryListScreen(viewModel)
}
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview

fun DiaryListScreen(@PreviewParameter(DiaryViewModelProvider::class) viewModel: DiaryViewModel) {
    val state = viewModel.viewState.collectAsState()
    Scaffold(

            topBar = {
                TopAppBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        title = {

                            Text(text = "Diary",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis) // titl
                        }
                )
            },
            content = { padding ->
                    val whatstate = state.value.diaryEntries.observeAsState(emptyList())
                    LazyColumn(Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(colorResource(R.color.core_grey_05))) {
                        items(whatstate.value) { diaryEntry ->
                            DiaryEntryScreen(diaryEntry, {
                                viewModel.onAction(UiAction.ClickDiaryEntry(it))
                            }, {
                                viewModel.onAction(UiAction.ClickExerciseEntry(it))
                            },{
                                viewModel.onAction(UiAction.ClickPersonalRecord(it))
                            }, {
                                viewModel.onAction(UiAction.ClickComment(it))
                            })
                        }
                    }

            },
            bottomBar = {
                //BottomNavigationComposable(BottomNavItem.Diary)
            }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Preview
@Composable
fun GenericCommentDialog(show: MutableState<String?> = mutableStateOf("Test")
) {
    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (show.value != null) {
        Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    show.value = null
                },

                content = {
                    Card(modifier = Modifier.padding(28.dp)) {
                        Column{
                            Text(text = "Comment",
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(all = 20.dp)
                            )
                            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                            Text(text = "${show.value}",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(all = 20.dp)
                            )
                        }
                    }
                }
        )
    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DiaryEntryScreen(@PreviewParameter(DiaryEntryDataProvider::class)
                     diaryEntry: DiaryEntry,
                     diaryEntryClick: ((DiaryEntry) -> Unit)? = null,
                     exerciseEntryClick : ((ExerciseEntry) -> Unit)? = null,
                             recordsClick : ((ExerciseEntry) -> Unit)? = null,
                     commentClick : ((ExerciseEntry) -> Unit)? = null
                     ) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, top = 15.dp, end = 15.dp), elevation = 15.dp) {
        Column() {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    diaryEntryClick?.invoke(diaryEntry)
                }){
                Text(text = diaryEntry.dayString,
                        modifier = Modifier.padding(start = 15.dp,
                                top = 10.dp
                        ),
                        fontSize = 26.sp
                )
                Text(
                        text = diaryEntry.dateString,
                        modifier = Modifier
                            .padding(
                                start = 15.dp,
                                top = 2.dp,
                                bottom = 4.dp
                            )
                            .alpha(ContentAlpha.medium),
                )
            }
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
            val entriesState = diaryEntry.exerciseEntries.observeAsState()
            Column(modifier = Modifier.fillMaxWidth()){

                entriesState.value?.forEach{ exerciseEntry ->
                    ExerciseEntryScreen(exerciseEntry, exerciseEntryClick, recordsClick, commentClick)
                }
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun ExerciseEntryScreen(@PreviewParameter(ExerciseEntryDataProvider::class)
                        exerciseEntry: LiveData<ExerciseEntry>,
                        exerciseEntryClick : ((ExerciseEntry) -> Unit)? = null,
                        recordsClick : ((ExerciseEntry) -> Unit)? = null,
                        commentClick : ((ExerciseEntry) -> Unit)? = null
                        ) {

    val stateStuff = exerciseEntry.observeAsState(ExerciseEntryImpl("testname","testamount",
            Color.Red.toArgb(),
            true,true,false, emptyList(),WorkoutExercise()))
    if(stateStuff.value.exerciseName == "null"){
        throw Exception("can't show a null placeholdler")
    }
    Card(modifier = Modifier.clickable { exerciseEntryClick?.invoke(stateStuff.value!!) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                    .padding(start = 15.dp)
                    .height(71.dp)
            ) {
                Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(stateStuff.value.color))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = stateStuff.value.exerciseName,
                        modifier = Modifier.padding(
                                start = 15.dp,
                                top = 15.dp), fontSize = 18.sp)
                Text(text = stateStuff.value.amountOfSets,
                        modifier = Modifier
                                .padding(
                                        start = 15.dp,
                                        top = 2.dp,
                                        bottom = 10.dp
                                )
                                .alpha(ContentAlpha.medium), fontSize = 14.sp)
            }

            if (stateStuff.value.showComment) {
                Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                        //.background(Color.Red)
                        //.width(48.dp)
                        .padding(start = 15.dp)
                        .height(71.dp)
                ) {

                    Icon(Icons.Filled.Comment, "Multiple Prs",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                    .padding(end = if (stateStuff.value.showPrOnly || stateStuff.value.showFire) {
                                        0.dp
                                    } else {
                                        15.dp
                                    })
                                    .clickable {
                                        commentClick?.invoke(stateStuff.value)
                                    }
                    )
                }
            }


            if (stateStuff.value.showFire) {

                Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                        //.background(Color.Red)
                        //.width(48.dp)
                        .padding(start = 15.dp)
                        .height(71.dp)
                ) {

                    Icon(Icons.Filled.Whatshot,
                            "Multiple Prs",
                            tint = Color.Red,
                            modifier = Modifier
                                    .padding(end = 15.dp)
                                    .clickable {
                                        recordsClick?.invoke(stateStuff.value)
                                    }
                    )


                }
            } else if (stateStuff.value.showPrOnly) {
                Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                        //.background(Color.Red)
                        //.width(48.dp)
                        .padding(start = 15.dp)
                        .height(71.dp)
                ) {
                    Icon(Icons.Filled.EmojiEvents, " Prs",
                            tint = Color.Red,
                            modifier = Modifier
                                    .padding(end = 15.dp)
                                    .clickable {
                                        recordsClick?.invoke(stateStuff.value)
                                    }
                    )
                }
            }
        }
        }
    }



@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun ShowRecords(records: List<String> = listOf("Volume PR",
        "One Rep Max PR",
        "Estimated One Rep Max PR",
        "Maximum Repetitions PR",
        "Maximum Weight PR",
        "Harder Than Last Time"
)
){
    Column{
        val title = if(records.count()> 1 ){
            "Multiple Records"
        } else {
            "Record"
        }
        Text(text = title,
                color = MaterialTheme.colors.primary,
                fontSize = 22.sp,
                modifier = Modifier.padding(all = 20.dp)
        )
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        records.forEach {
            Text(text = it,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
            )
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.alpha(.2f))
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@Composable
fun ShowRecordsDialog( records: MutableState<List<String>?> ){

    //val showDialog : MutableState<Boolean> = remember{mutableStateOf(show)}
    if (records.value != null) {
        Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = {
                    records.value = null
                },

                content = {
                    Card(modifier = Modifier.padding(28.dp)) {
                        records.value?.let {
                            ShowRecords(it)
                        }
                    }
                }
        )
    }
}

//android:layout_marginTop="25dp"
//android:layout_marginEnd="15dp"
//android:layout_marginRight="15dp"
//android:background="@android:color/transparent"
//android:tint="@color/colorPrimary"
//android:visibility="visible"


class DiaryEntryDataProvider : PreviewParameterProvider<DiaryEntry> {
    override val values = getSampleDiaryEntryData()
}

fun getSampleDiaryEntryData(): Sequence<DiaryEntry> {
    return sequenceOf(
            DiaryEntryViewOnly(
                    "Saturday",
                    "March 12, 2022", MutableLiveData(getSampleExerciseEntryData().map { MutableLiveData(it) }.toList()),
            ),
            DiaryEntryViewOnly(
                    "Friday",
                    "March 11, 2022",
                MutableLiveData(listOf(MutableLiveData(
                    MockExerciseEntry(exerciseName = "Flat Barbell Bench Press",
                            amountOfSets = "5 sets",
                            color = Color.Blue.toArgb(),
                            showFire = false,
                            showPrOnly = true,
                            showComment = false,
                            records = listOf("Personal Record")))),
            ),
            ),
            DiaryEntryViewOnly(
                    "Thursday",
                    "March 10, 2022",
                MutableLiveData(listOf(MutableLiveData(
                            MockExerciseEntry("Chin Up",
                            "8 sets",
                            Color.Blue.toArgb(), true, showPrOnly = true, showComment = false,listOf("Personal Record")))),
            ),
            ),
    )
}
class ExerciseEntryDataProvider : PreviewParameterProvider<ExerciseEntry> {
    override val values = getSampleExerciseEntryData()
}

fun getSampleExerciseEntryData(): Sequence<ExerciseEntry> {
    return sequenceOf(
            MockExerciseEntry("Chin Up",
                    "9 sets",
                    Color.Green.toArgb(), true, true,false,listOf("Personal Record")),
            MockExerciseEntry("Flat Barbell Bench Press",
                    "6 sets",
                    Color.Blue.toArgb(), true, true,true, listOf("Personal Record")),
            MockExerciseEntry("Incline Barbell Bench Press",
                    "1 sets",
                    Color.Blue.toArgb(), true,true,false, listOf("Personal Record")),
    )
}

class DiaryViewModelProvider : PreviewParameterProvider<DiaryViewModel> {
    override val values = sequenceOf(
            DiaryViewModel(
                MockFetchDiaryUseCase(getSampleDiaryEntryData().toList()),
                NoOpNavigateToAddExerciseUseCase(),
                NoOpNavigateToDayActivityUseCase(),
                NoOpNavigateToCommentUseCase(),
                MockNavigateToDiaryDayUseCase(),
                null
            )
    )
}







// public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight,String Comment)
class MockDiaryViewModelFactory2(
    val context: Context,
    val knownExerciseService: KnownExerciseService,
    val GoToAddExerciseUseCase: NavigateToAddExerciseUseCase,
    val NavigateToCommentUseCase: NavigateToCommentUseCase,
    val NavigateToDiaryDayUseCase: NavigateToDiaryDayUseCase,
    val savedStateHandle: SavedStateHandle?,
    val NavigateToExerciseEntryStatsUseCase: NavigateToExerciseEntryStatsUseCase = NoOpNavigateToExerciseEntryStatsUseCase()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(
                FetchDiaryUseCase = FetchDiaryUseCaseImpl(WorkoutServiceSingleton.getWorkoutService(context), knownExerciseService),
            GoToAddExerciseUseCase = GoToAddExerciseUseCase,
            NoOpNavigateToDayActivityUseCase(),
            NavigateToCommentUseCase = NavigateToCommentUseCase,
            NavigateToDiaryDayUseCase = NavigateToDiaryDayUseCase,
            savedStateHandle,
            NavigateToExerciseEntryStatsUseCase =
                NavigateToExerciseEntryStatsUseCase

        ) as T
    }
}




