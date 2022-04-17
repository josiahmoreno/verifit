package com.example.verifit.singleday

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.KnownExerciseService
import com.example.verifit.KnownExerciseServiceImpl
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.diary.*
import com.example.verifit.diary.MockDiaryViewModelFactory2
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi


@ExperimentalComposeUiApi
class Compose_DayActivity : AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: DayViewModel by viewModels {
        MockDayViewModelFactory()
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme  {
                DayListScreen(viewModel)
                Log.d("Diary","SetContent Finished")
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview

fun DayListScreen(@PreviewParameter(DiaryViewModelProvider::class) viewModel: DayViewModel) {

}

class DiaryViewModelProvider : PreviewParameterProvider<DayViewModel> {
    override val values = sequenceOf(
            DayViewModel(
            )
    )
}

// public WorkoutSet(String Date, String Exercise, String Category, Double Reps, Double Weight,String Comment)
class MockDayViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DayViewModel(
        ) as T
    }
}