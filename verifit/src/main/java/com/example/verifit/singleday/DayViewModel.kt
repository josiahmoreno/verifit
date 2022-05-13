package com.example.verifit.singleday

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.*
import com.example.verifit.diary.DialogData
import com.example.verifit.diary.DiaryEntry
import com.example.verifit.diary.ExerciseEntry
import com.example.verifit.diary.ExerciseEntryStats
import com.example.verifit.main.BaseViewModel
import com.example.verifit.main.SingleViewPagerScreenData
import com.example.verifit.main.WorkoutExercisesViewData
import com.example.verifit.singleton.DateSelectStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(val fetchDaysWorkoutsUseCase: FetchDaysWorkoutsUseCase,
                                       val NavigateToExercisesListUseCase : NavigateToExercisesListUseCase = MockNavigateToExercisesListUseCase(),
                                       val NavigateToAddExerciseUseCase: NavigateToAddExerciseUseCase = NoOpNavigateToAddExerciseUseCase(),
                                       val NavigateToDiaryUseCase: NavigateToDiaryListUseCase = NoOpNavigateToDiaryListUseCase(),
                                       val NavigateToViewPagerUseCase: NavigateToViewPagerUseCase = NoOpNavigateToViewPagerUseCase(),
                                       val savedStateHandle: SavedStateHandle?
                   )
        : BaseViewModel<ViewState, UiAction, OneShotEvents>(
            initialViewState = ViewState(data =
                    fetchDaysWorkoutsUseCase(savedStateHandle?.date!!).data,
                    date = calcDateString(savedStateHandle.date!!))
    ) {
    val date = savedStateHandle?.date!!
        override fun onAction(uiAction: UiAction) {
            when (uiAction) {
                is UiAction.GoToExercisesList -> NavigateToExercisesListUseCase(date = date)
                is UiAction.GoToAddExercises -> NavigateToAddExerciseUseCase(uiAction.workoutExercise.exercise, date = date)
                UiAction.GoToMainViewPager -> {
                    Log.d("ViewPagerViewModel","day to Viewpager = $date")
                    NavigateToViewPagerUseCase(date)
                }
                UiAction.GoToDiaryWithDay ->NavigateToDiaryUseCase(date)
            }
        }

    private fun fetchDate(data: FetchDaysWorkoutsUseCase.Results): String {
        return when(data){
            is FetchDaysWorkoutsUseCase.ResultsImpl -> {
                data.day.date
            }
            else -> {
                "2222-22-22"
            }

        }
    }


    companion object {
        fun calcDateString(dateString :  String) : String{
            val parsed = SimpleDateFormat("yyyy-MM-dd").parse(dateString)
            val monthDateYearFormat: DateFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
            val nameOfDayString = monthDateYearFormat.format(parsed)
            return nameOfDayString
        }
    }


    private fun Add(num : Int): Int{
        return num +2
    }

}


data class ViewState(
        val data: WorkoutExercisesViewData,
        val date: String
)

sealed class UiAction{
    object GoToExercisesList : UiAction()
    object GoToMainViewPager : UiAction()
    object GoToDiaryWithDay : UiAction()

    class GoToAddExercises(val workoutExercise: WorkoutExercise): UiAction()

}
sealed class OneShotEvents{

}