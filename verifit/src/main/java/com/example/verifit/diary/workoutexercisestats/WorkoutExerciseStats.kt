package com.example.verifit.diary.workoutexercisestats

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
import androidx.navigation.NavHostController
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.common.NavigateToAddExerciseUseCaseImpl
import com.example.verifit.diary.CalculatedExerciseEntryUseCaseImpl
import com.example.verifit.diary.GenericStatsWithButtons

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun WorkoutExerciseStatsContent(
    navHostController: NavHostController,exerciseName: String?,
                                date: String?,
                                closeClick :(()-> Unit)? = null )
{
    val viewModel = WorkoutExerciseStatsViewModel(exerciseName = exerciseName!!,
        date = date!!,
        CalculatedExerciseEntryUseCase = CalculatedExerciseEntryUseCaseImpl(WorkoutServiceSingleton.getWorkoutService(LocalContext.current)),
        NavigateToAddExerciseUseCase = NavigateToAddExerciseUseCaseImpl(navHostController, "diary_list?date={date}")
        )
    val state = viewModel.viewState.collectAsState()
    Card(modifier = Modifier.padding(28.dp)) {
        GenericStatsWithButtons(state = state.value.data,
            leftButtonClick = { viewModel.onAction(UiAction.EditExercise)} ,
            rightButtonClick = {closeClick?.invoke() },
            leftTitle = "Edit",
            leftImageVector = Icons.Filled.Edit
            )
    }
}