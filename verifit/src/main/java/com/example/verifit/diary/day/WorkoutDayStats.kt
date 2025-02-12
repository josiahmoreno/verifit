package com.example.verifit.diary.day

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.common.NavigateToDayActivityUseCaseImpl
import com.example.verifit.diary.CalculatedDiaryEntryUseCaseImpl
import com.example.verifit.diary.GenericStatsWithButtons



@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun DiaryDayContentHilt(closeClick :(()-> Unit)? = null){
    val viewModel : DiaryDayStatsViewModel = hiltViewModel()
    val state = viewModel.viewState.collectAsState()
    Card(modifier = Modifier.padding(28.dp)) {
        GenericStatsWithButtons(state = state.value.data,
            leftButtonClick = { viewModel.onAction(UiAction.ViewDayClick)} ,
            rightButtonClick = {closeClick?.invoke() })
    }
}