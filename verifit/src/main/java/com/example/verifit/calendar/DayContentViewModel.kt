package com.example.verifit.calendar.daycontent


import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.verifit.ColorGetter
import com.example.verifit.WorkoutDay
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DayContentViewModel @Inject constructor(
    val workoutService: WorkoutService,
    val colorGetter: ColorGetter
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState.initialState("" )
) {

    override fun onAction(uiAction: UiAction) {
        when(uiAction){

        }
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    fun fetchForDate(date: LocalDate): LiveData<List<Int>> {

        val date_str = formatter.format(date)
        return workoutService.fetchDayLive(date_str).map { workoutDay ->
            val categorySet = linkedSetOf<String>()
            workoutDay.exercises.forEach { workoutExercise ->
                categorySet.add(workoutExercise.exercise)
            }
            categorySet.map { exerciseName ->
                colorGetter.getCategoryIconTint(exerciseName)
            }
        }
    }

}


 class ViewState(val categorys: Iterable<Color>) {
    companion object{
        fun initialState(date: String?): ViewState {
            //TODO()
            Log.d("Calendar","viewModel init")
            return ViewState(categorys = listOf(Color.Red,Color.Green))
        }
    }

}

sealed class UiAction{


}
sealed class OneShotEvents{


}