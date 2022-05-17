package com.example.verifit.exercises

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.verifit.*
import com.example.verifit.common.*
import com.example.verifit.navigationhost.AuroraNavigator
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalComposeUiApi
class Compose_ExercisesActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: ExercisesListViewModel by viewModels {
        ExercisesListViewModelFactory(applicationContext = this,
            knownExerciseService = KnownExerciseServiceSingleton.getKnownExerciseService(applicationContext),
            date = ""
        )
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                ExercisesList(viewModel)
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExercisesListHilt(
) {
    val viewModel: ExercisesListViewModel =  hiltViewModel()
    ExercisesList(
            viewModel
    )
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExercisesList(
    viewModel: ExercisesListViewModel,
) {
    val state = viewModel.viewState.collectAsState()
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyScrollState = rememberLazyListState()
    BackPressHandler(onBackPressed = {viewModel.onAction(UiAction.OnBackPress)})
    MaterialTheme() {
        Scaffold(

            topBar = {
                TopAppBar(
                    navigationIcon =
                        if(state.value.showSearch){
                            {
                                IconButton(onClick = {
                                    //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                    viewModel.onAction(UiAction.ExitSearch)
                                }) {
                                    Icon(Icons.Filled.ArrowBack, "Exit Search")
                                }
                            }
                        } else {
                               null
                        }
                    ,
                    backgroundColor = MaterialTheme.colors.primary,
                    title = {
                        if(state.value.showSearch){
                                TextField(value = state.value.searchingString, onValueChange = {
                                    viewModel.onAction(UiAction.Searching(it))
                                },
                                    colors =  TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        focusedLabelColor = Color.Red,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.White
                                        ),

                                    placeholder = {
                                        Text("Search...", color = Color.White, modifier = Modifier.alpha(.6f), fontSize = 18.sp )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .onFocusChanged {
                                            if (it.isFocused) {
                                                keyboardController?.show()
                                            }
                                        }
                                        .fillMaxHeight()
                                        .fillMaxWidth())
                            DisposableEffect(Unit) {
                                focusRequester.requestFocus()
                                onDispose { }
                            }

                           
                        } else {
                            Text(
                                    text = "Exercises",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,

                                    ) // titl
                        }

                    },
                    actions = {
                        if(state.value.showSearch){
                            if(state.value.showClearSearch){
                                IconButton(onClick = {
                                    //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                    viewModel.onAction(UiAction.ClearSearch)
                                }) {
                                    Icon(Icons.Filled.Clear, "Search")
                                }
                            }
                        } else {
                            IconButton(onClick = {
                                //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                viewModel.onAction(UiAction.OpenSearch)
                            }) {
                                Icon(Icons.Filled.Search, "Search")
                            }
                        }

                        IconButton(onClick = {
                            //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                            viewModel.onAction(UiAction.StartEdit)
                        }) {
                            Icon(Icons.Filled.Brush, "Edit")
                        }
                    }
                )
            },
            content = { padding ->
                LazyColumn(state = lazyScrollState){
                    val result = state.value.ExerciseListResult2
                  when(result){
                      is ExerciseListResult2.Category -> {
                          items(result.data){ item ->
                                CategoryItem(item.workoutCategory){
                                    viewModel.onAction(UiAction.CategoryClick(item))
                                }
                              Divider(thickness = 1.dp)
                          }

                      }
                      is ExerciseListResult2.Exercises -> items(result.data){ item ->
                          VerifitExercisesItem(item){
                              viewModel.onAction(UiAction.ExerciseClick(it))
                          }
                      }

                  }                 }
            },
                bottomBar = {
                    //BottomNavigationComposable(BottomNavItem.Exercises)
                }
        )

    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun CategoryItem(
    category: WorkoutCategory = WorkoutCategory("Biceps"),
    click : ((WorkoutCategory) -> Unit)? = null
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { click?.invoke(category) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(category.category, modifier = Modifier.padding(start = 15.dp, top = 15.dp, bottom = 15.dp).weight(1f), fontSize = 18.sp)
            Icon(Icons.Filled.ChevronRight, "")
        }
    }
}


@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun VerifitExercisesItem(
    exercise: Exercise = Exercise("Dumbell Curl","Biceps"),
    click : ((Exercise) -> Unit)? = null
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { click?.invoke(exercise) }) {
        Column {
            Text(exercise.name, modifier = Modifier.padding(start = 15.dp, top = 15.dp), fontSize = 18.sp)
            Text(
                    exercise.bodyPart,
                    modifier = Modifier
                        .padding(start = 15.dp, top = 2.dp, bottom = 10.dp)
                        .alpha(.6f),
                    fontSize = 14.sp,
            )
        }
    }

}

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit,
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}

data class ExercisesListDataResult(
    val results: List<ExerciseListResult>
)

sealed class ExerciseListResult(val name: String) {
    class Category(val workoutCategory: WorkoutCategory, val items: List<ExerciseItem>): ExerciseListResult(workoutCategory.category)
    class ExerciseItem(val exercise: Exercise): ExerciseListResult(exercise.name)
}

sealed class ExerciseListResult2() {
    class Category(val data :List<WorkoutCategoryItem>): ExerciseListResult2()
    class Exercises(val data: List<Exercise>): ExerciseListResult2()
}

class WorkoutCategoryItem(val workoutCategory: WorkoutCategory, val items: List<Exercise>) {

}

class ExercisesListViewModelFactory(
    val applicationContext: Context,
    val knownExerciseService: KnownExerciseService,
    val date: String?,
    val NavigateToAddExerciseUseCase : NavigateToAddExerciseUseCase = NoOpNavigateToAddExerciseUseCase(),
    val goToNewCustomExercise: GoToNewCustomExerciseCase = NoOpNavigateToNewCustomExerciseCase(),
    val navHostController: AuroraNavigator? = null




) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExercisesListViewModel(
            FetchExercisesListUseCase(knownExerciseService),
            GoToAddExerciseUseCase = NavigateToAddExerciseUseCase ,
            GoToNewCustomExerciseCase = goToNewCustomExercise,
            savedStateHandle  = null, navHostController!!
        ) as T
    }
}

