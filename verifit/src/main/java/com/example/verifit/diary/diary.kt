package com.example.verifit.diary

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.KnownExerciseService
import com.example.verifit.KnownExerciseServiceImpl
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.WorkoutSet
import com.example.verifit.main.OnLifecycleEvent
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.FakeWorkoutService2
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import java.util.*

@ExperimentalComposeUiApi
class Compose_DiaryActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DiaryViewModel by viewModels {
        //DiaryViewModelFactory(WorkoutServiceSingleton.getWorkoutService(context = applicationContext),
            //KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
        MockDiaryViewModelFactory2(KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                DiaryListScreen(viewModel)
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview

fun DiaryListScreen(@PreviewParameter(DiaryViewModelProvider::class) viewModel: DiaryViewModel) {
    val state = viewModel.viewState.collectAsState()
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            -> {
                viewModel.onAction(UiAction.OnResume)
            }
            else -> Unit

        }
    }
    Scaffold(
            drawerContent = { /*...*/ },
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
            content = {
                LazyColumn(Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)) {
                    items(state.value.diaryEntries) { diaryEntry ->
                        DiaryEntryScreen(diaryEntry, {
                            viewModel.onAction(UiAction.ClickDiaryEntry(it))
                        }, {
                            viewModel.onAction(UiAction.ClickExerciseEntry)
                        })
                    }
                }

                state.value.showDiaryStats?.let{ dialogData ->
                    GenericStatsWithButtonDialog(
                            state = dialogData,
                            dismissRequest = { viewModel.onAction(UiAction.DiaryEntryDialogDismiss) },
                            view = {},
                            close = { viewModel.onAction(UiAction.DiaryEntryDialogDismiss) }
                    )
                }
            }
    )
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DiaryEntryScreen(@PreviewParameter(DiaryEntryDataProvider::class)
                     diaryEntry: DiaryEntry,
                     diaryEntryClick: ((DiaryEntry) -> Unit)? = null,
                     exerciseEntryClick : ((ExerciseEntry) -> Unit)? = null
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
            Column(modifier = Modifier.fillMaxWidth()){
                diaryEntry.exerciseEntries.forEach{ exerciseEntry ->
                    ExerciseEntryScreen(exerciseEntry, exerciseEntryClick)
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
                        exerciseEntry: ExerciseEntry,
                        exerciseEntryClick : ((ExerciseEntry) -> Unit)? = null) {
    Card(modifier = Modifier.clickable { exerciseEntryClick?.invoke(exerciseEntry) }) {
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
                                .background(Color(exerciseEntry.color))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = exerciseEntry.exerciseName,
                        modifier = Modifier.padding(
                                start = 15.dp,
                                top = 15.dp), fontSize = 18.sp)
                Text(text = exerciseEntry.amountOfSets,
                        modifier = Modifier
                                .padding(
                                        start = 15.dp,
                                        top = 2.dp,
                                        bottom = 10.dp
                                )
                                .alpha(ContentAlpha.medium), fontSize = 14.sp)
            }
            Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                    .padding(start = 15.dp)
                    .height(71.dp)
            ) {
                if(exerciseEntry.showFire){
                    Icon(Icons.Filled.Whatshot, "Multiple Prs",
                        tint = Color.Red,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                } else if (exerciseEntry.showPrOnly){
                    Icon(Icons.Filled.EmojiEvents, " Prs",
                        tint = Color.Red,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                }

            }
        }
    }

}

@ExperimentalMaterialApi
@Preview
@Composable
fun ShowRecords(){
    Text(text = "Graph",
        color = MaterialTheme.colors.primary,
        fontSize = 22.sp,
        modifier = Modifier.padding(all = 20.dp)
    )
    Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
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
            DiaryEntryImpl(
                    "Saturday",
                    "March 12, 2022", getSampleExerciseEntryData().toList(),
            ),
            DiaryEntryImpl(
                    "Friday",
                    "March 11, 2022",
                    listOf(
                    ExerciseEntry("Flat Barbell Bench Press",
                            "5 sets",
                            Color.Blue.toArgb(), false,true, showComment = false),
            ),
            ),
            DiaryEntryImpl(
                    "Thursday",
                    "March 10, 2022",
                    listOf(
                    ExerciseEntry("Chin Up",
                            "8 sets",
                            Color.Blue.toArgb(), true, showPrOnly = true, showComment = false),
            ),
            ),
    )
}
class ExerciseEntryDataProvider : PreviewParameterProvider<ExerciseEntry> {
    override val values = getSampleExerciseEntryData()
}

fun getSampleExerciseEntryData(): Sequence<ExerciseEntry> {
    return sequenceOf(
            ExerciseEntry("Chin Up",
                    "9 sets",
                    Color.Green.toArgb(), true, true,false),
            ExerciseEntry("Flat Barbell Bench Press",
                    "6 sets",
                    Color.Blue.toArgb(), true, true,true),
            ExerciseEntry("Incline Barbell Bench Press",
                    "1 sets",
                    Color.Blue.toArgb(), true,true,false),
    )
}

class DiaryViewModelProvider : PreviewParameterProvider<DiaryViewModel> {
    override val values = sequenceOf(
            DiaryViewModel(
                    MockFetchDiaryUseCase(getSampleDiaryEntryData().toList()), MockCalculatedDiaryEntryUseCase()
            )
    )
}




class DiaryViewModelFactory(
    val workoutService: WorkoutService,
    val knownExerciseService: KnownExerciseService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(
                FetchDiaryUseCaseImpl(workoutService, knownExerciseService), CalculatedDiaryEntryUseCaseImpl()
        ) as T
    }
}

class MockDiaryViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(
            MockFetchDiaryUseCase(getSampleDiaryEntryData().toList()), MockCalculatedDiaryEntryUseCase()
        ) as T
    }
}

// public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight,String Comment)
class MockDiaryViewModelFactory2(
    val knownExerciseService: KnownExerciseService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiaryViewModel(
            FetchDiaryUseCaseImpl(FakeWorkoutService2(DateSelectStore), knownExerciseService), CalculatedDiaryEntryUseCaseImpl()
        ) as T
    }
}




