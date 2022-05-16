package com.example.verifit.calendar

import androidx.lifecycle.*
import com.example.verifit.ColorGetter
import com.example.verifit.WorkoutCategory
import com.example.verifit.WorkoutDay
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.NavigateToDayDialogUseCase
import com.example.verifit.common.NavigateToDayDialogUseCaseImpl
import com.example.verifit.main.BaseViewModel
import com.example.verifit.workoutservice.WorkoutService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val workoutService: WorkoutService,
    val colorGetter: ColorGetter,
    val NavigateToDayDialogUseCase: NavigateToDayDialogUseCase
): BaseViewModel<ViewState, UiAction, OneShotEvents>(
    initialViewState = ViewState.initialState(savedStateHandle.date,workoutService, colorGetter)
) {
    val date = savedStateHandle.date
    init {
        viewModelScope.launch {
            val transformed = workoutService.fetchWorkoutDaysLive().map { list ->
                ViewState.map(list, colorGetter = colorGetter)
            }.asFlow().collect{
                _viewState.value = viewState.value.copy(categoryData = it)
            }
        }

    }
    override fun onAction(uiAction: UiAction) {
        when(uiAction){
            is UiAction.OnDateClick -> {
                if(viewState.value.currentSelection == uiAction.localDate){
                    NavigateToDayDialogUseCase(uiAction.localDate.format(formatter))
                } else {
                    _viewState.value = viewState.value.copy(currentSelection = uiAction.localDate)
                    val date = uiAction.localDate.format(formatter)
                    (workoutService.fetchWorkoutDays()).firstOrNull{
                        it.date == date
                    }?.let {
                        NavigateToDayDialogUseCase(it.date)
                    }

                }

            }
            UiAction.GoToToday -> {
                viewModelScope.launch {
                    _oneShotEvents.send(OneShotEvents.ScrollToMonth(YearMonth.now()))
                }
            }
        }
    }

}


 data class ViewState(val currentSelection: LocalDate?, val categoryData: HashMap<String, DayViewLiveData>) {



     companion object {
         fun initialState(date: String?, workoutService: WorkoutService, colorGetter: ColorGetter): ViewState {
             //TODO()

                val initialMap = map(workoutService.fetchWorkoutDaysLive().value!!, colorGetter = colorGetter)
             return ViewState(currentSelection = LocalDate.parse(date, formatter),initialMap)
         }


         fun map(list: List<WorkoutDay>, colorGetter: ColorGetter) : HashMap<String, DayViewLiveData> {
             val hashMap = HashMap<String,DayViewLiveData>()
             val tempCategoryMap = HashSet<String>()
             list.forEach { workoutDay ->
                 tempCategoryMap.clear()
                 workoutDay.exercises.filter { it.sets.isNotEmpty() }.forEach { workoutExercise ->

                     val category =  workoutExercise.sets[0].category

                     if(!tempCategoryMap.contains(category)){

                         tempCategoryMap.add(category)
                     }
                 }
                 val tempList = tempCategoryMap.toList().sorted().map { category ->
                     colorGetter.getCategoryIconTint(WorkoutCategory(category))
                 }

                 if(!hashMap.containsKey(workoutDay.date)){
                     hashMap[workoutDay.date] = DayViewLiveData(tempList)
                 }
             }
             return hashMap
         }
     }


}

class DayViewLiveData(val categories: List<Int>) {

}

sealed class UiAction{
    object GoToToday : UiAction()

    class OnDateClick(val localDate: LocalDate): UiAction()
}
sealed class OneShotEvents{
    class ScrollToMonth(val now: YearMonth) : OneShotEvents()

}