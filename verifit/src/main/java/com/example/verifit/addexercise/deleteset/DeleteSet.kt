package com.example.verifit.addexercise.deleteset

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.addexercise.composables.Delete
import com.example.verifit.common.NavigateToAddExerciseUseCase
import com.example.verifit.common.NavigateToAddExerciseUseCaseImpl
import com.example.verifit.common.NavigateToDayActivityUseCaseImpl
import com.example.verifit.diary.CalculatedDiaryEntryUseCaseImpl
import com.example.verifit.diary.CalculatedExerciseEntryUseCaseImpl
import com.example.verifit.diary.GenericStatsWithButtons

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun DeleteSetContent(
    navHostController: NavHostController,identifier: String,
                                closeClick :(()-> Unit)? = null )
{
    Log.d("deleteset","set identifier $identifier")
    val viewModel = DeleteSetViewModel( DeleteSetUseCase = DeleteSetUseCase(WorkoutServiceSingleton.getWorkoutService(
        LocalContext.current)), savedStateHandle = navHostController.currentBackStackEntry?.savedStateHandle!!)
    Card(modifier = Modifier.padding(28.dp)) {
        Delete({

            viewModel.onAction(UiAction.DeleteSet)
            navHostController.previousBackStackEntry?.savedStateHandle?.set("delete_set_result", true)
            navHostController.popBackStack()
        }, {

            navHostController.popBackStack()
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun DeleteSetContentHilt( )
{

    val viewModel : DeleteSetViewModel = hiltViewModel()
    val navHostController : NavHostController = rememberNavController()
    Card(modifier = Modifier.padding(28.dp)) {
        Delete({

            viewModel.onAction(UiAction.DeleteSet)
            navHostController.previousBackStackEntry?.savedStateHandle?.set("delete_set_result", true)
            navHostController.popBackStack()
        }, {

            navHostController.popBackStack()
        })
    }
}