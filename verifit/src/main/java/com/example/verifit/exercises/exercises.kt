package com.example.verifit.exercises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.*
import com.example.verifit.customexercise.Compose_CustomExerciseActivity
import com.example.verifit.main.OnLifecycleEvent
import com.example.verifit.main.getActivity
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@ExperimentalComposeUiApi
class Compose_ExercisesActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: ExercisesListViewModel by viewModels {
        ExercisesListViewModelFactory(this, KnownExerciseServiceImpl.getKnownExerciseService(applicationContext))
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
fun ExercisesList(
    viewModel: ExercisesListViewModel,
) {
    val state = viewModel.viewState.collectAsState()
    val context = LocalContext.current

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            -> {
                viewModel.onAction(UiAction.OnStart)
            }
            else -> Unit
        }
    }
    LaunchedEffect(key1 = "ExercisesList", block = {

        viewModel.oneShotEvents
            .onEach {
                when (it) {
                    is OneShotEvents.GoToAddExercise -> {
                        val `in` = Intent(context, Compose_AddExerciseActivity::class.java)
                        `in`.putExtra("exercise", it.exerciseName)

                        context.startActivity(`in`)
                        context.getActivity()?.overridePendingTransition(0, 0)
                    }
                    OneShotEvents.GoToNewCustomExercise -> {
                        val `in` = Intent(context, Compose_CustomExerciseActivity::class.java)
                        context.startActivity(`in`)
                    }
                }
            }
            .collect()
    })
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    MaterialTheme() {
        Scaffold(
            drawerContent = { /*...*/ },
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
            content = {
                LazyColumn(){
                  items(state.value.ExercisesListDataResult.exercises.count())  { exercise ->
                      ExercisesItem(state.value.ExercisesListDataResult.exercises[exercise]) {
                          viewModel.onAction(UiAction.ExerciseClick(it))
                      }
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
fun ExercisesItem(
    exercise: Exercise,
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

data class ExercisesListDataResult(
    val exercises : List<Exercise>
)

class ExercisesListViewModelFactory(
    val applicationContext: Context,
    val knownExerciseService: KnownExerciseService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExercisesListViewModel(
            FetchExercisesListUseCase(knownExerciseService)
        ) as T
    }
}

