package com.example.verifit.customexercise

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.verifit.KnownExerciseService
import com.example.verifit.KnownExerciseServiceSingleton
import com.example.verifit.common.MockNavigateToExercisesListUseCase
import com.example.verifit.common.NavigateToExercisesListUseCase
import com.example.verifit.common.NavigateToExercisesListUseCaseImpl
import com.example.verifit.settings.ToastMaker
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalComposeUiApi
class Compose_CustomExerciseActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: CustomExerciseViewModel by viewModels {
        CustomExerciseViewModelFactory(KnownExerciseServiceSingleton.getKnownExerciseService(applicationContext), applicationContext = this, ToastMaker(this),
            MockNavigateToExercisesListUseCase(
            ))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                CustomExerciseScreen(viewModel)
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomExerciseScreen(navHostController: NavHostController){
    val context = LocalContext.current
    val viewModel: CustomExerciseViewModel = viewModel (factory =
    CustomExerciseViewModelFactory(KnownExerciseServiceSingleton.getKnownExerciseService(context), applicationContext = context, ToastMaker(context),
        NavigateToExercisesListUseCaseImpl(navHostController))
    )
    CustomExerciseScreen(
        viewModel
    )
}
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomExerciseScreen(viewModel: CustomExerciseViewModel){
    val state = viewModel.viewState.collectAsState()
    Scaffold(

            topBar = {
                TopAppBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        title = {

                            Text(text = "New Exercise",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis) // titl
                        },
                        actions = {
                            IconButton(onClick = {
                                viewModel.onAction(UiAction.SaveNewExercise)
                            }) {
                                Icon(Icons.Filled.Check, "comment")
                            }
                        }
                )
            },
            content = { padding ->
                Column(){
                    Text("Choose Exercise Name:", fontSize = 24.sp, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 20.dp, top = 20.dp))
                    Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp), color = MaterialTheme.colors.primary)
                    TextField(
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                            value = state.value.exerciseNameString,
                            modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 0.dp),
                            onValueChange = {
                                viewModel.onAction(UiAction.OnExerciseName(it))
                            })
                    Text("Choose Exercise Category:", fontSize = 24.sp, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 20.dp, top = 20.dp))
                    Divider(thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp), color = MaterialTheme.colors.primary)
                    ExerciseCategorySpinner(state.value.categories,state.value.selectCategory) {
                        viewModel.onAction(UiAction.CategorySelected(it))
                    }

                }
            }
    )



}

@Composable
fun ExerciseCategorySpinner (specimens: List<String>, exercise: String, click: ((String) -> Unit)? = null) {
    var expanded = remember { mutableStateOf(false)}
    Box(Modifier.width(320.dp), contentAlignment = Alignment.Center) {
        Row(Modifier

                .padding(20.dp)
                .clickable {
                    expanded.value = !expanded.value
                }
                .align(alignment = Alignment.CenterStart)
                .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise, fontSize = 18.sp, modifier = Modifier
                    .padding(start = 8.dp,end = 8.dp)
                    .width(220.dp))
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            DropdownMenu(expanded = expanded.value, onDismissRequest = {expanded.value = false}

                    ,modifier = Modifier.width(220.dp)) {
                specimens.forEach { specimen ->
                    DropdownMenuItem(onClick = {
                        expanded.value = false

                           click?.invoke(specimen)
                        },modifier = Modifier.height(45.dp).fillMaxWidth()
                    ) {
                    Text(text = specimen.toString(), fontSize = 18.sp)
                }
                }
            }
        }
    }
}


class CustomExerciseViewModelFactory(
    val knownExerciseService: KnownExerciseService,
    val applicationContext: Context,
    val toastMaker: ToastMaker,
    val navigateToExercisesListUseCase: NavigateToExercisesListUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomExerciseViewModel(
                SaveNewExerciseUseCase(knownExerciseService,applicationContext,  toastMaker, navigateToExercisesListUseCase)
        ) as T
    }
}