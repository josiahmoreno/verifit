package com.example.verifit.singleday

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.verifit.WorkoutDay
import com.example.verifit.WorkoutExercise
import com.example.verifit.diary.DialogData
import com.example.verifit.diary.DiaryEntry
import com.example.verifit.diary.ExerciseEntry
import com.example.verifit.diary.ExerciseEntryStats
import com.example.verifit.main.BaseViewModel
import com.example.verifit.main.SingleViewPagerScreenData
import com.example.verifit.main.WorkoutExercisesViewData
import com.example.verifit.singleton.DateSelectStore
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat

class DayViewModel(val fetchDaysWorkoutsUseCase: FetchDaysWorkoutsUseCase)
        : BaseViewModel<ViewState, UiAction, OneShotEvents>(
            initialViewState = ViewState(data =
                    WorkoutExercisesViewData(MutableLiveData()),
                    date = "Monday, April 9 3333")
    ) {
    //val liveData: MutableLiveData<List<Pair<WorkoutExercise, Color>>> = MutableLiveData()
        lateinit var results: FetchDaysWorkoutsUseCase.Results

        override fun onAction(uiAction: UiAction) {
            when (uiAction) {
                UiAction.OnResume -> {
                    //viewState.value.data.workoutExercisesWithColors.value = FetchDaysWorkoutsUseCase()
                    results = fetchDaysWorkoutsUseCase()
                    _viewState.value = _viewState.value.copy(data = results.data,
                            date = calcDateString(fetchDate(results)))
                }
                is UiAction.GoToExercisesList -> viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.GoToExercisesList(fetchDate(results)))
                }
                is UiAction.GoToAddExercises -> viewModelScope.launch {
                    DateSelectStore.date_selected = uiAction.workoutExercise.date
                    _oneShotEvents.send(OneShotEvents.GoToAddExercise(uiAction.workoutExercise.exercise))
                }
                UiAction.GoToMainViewPager ->viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.GoToMainViewPager)
                }
                UiAction.GoToMainViewPager -> viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.GoToMainViewPager)
                }
                UiAction.GoToDiaryWithDay -> viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.GoToDiary(fetchDate(results)))
                }
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


    private fun calcDateString(dateString :  String) : String{
        val parsed = SimpleDateFormat("yyyy-MM-dd").parse(dateString)
        val monthDateYearFormat: DateFormat = SimpleDateFormat("EEEE, MMMM dd yyyy")
        val nameOfDayString = monthDateYearFormat.format(parsed)
        return nameOfDayString
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
    object OnResume : UiAction()
    object GoToExercisesList : UiAction()
    object GoToMainViewPager : UiAction()
    object GoToDiaryWithDay : UiAction()

    class GoToAddExercises(val workoutExercise: WorkoutExercise): UiAction()

}
sealed class OneShotEvents{
    class GoToAddExercise(val exerciseName: String): OneShotEvents()
    class GoToExercisesList(val dateString: String): OneShotEvents()
    class GoToDiary(val date: String): OneShotEvents()

    object GoToMainViewPager : OneShotEvents()
}