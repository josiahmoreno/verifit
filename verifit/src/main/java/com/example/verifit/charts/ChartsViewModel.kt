package com.example.verifit.charts

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.verifit.main.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class ChartsViewModel(val FetchChartsDataUseCase: FetchChartsDataUseCase)
    : BaseViewModel<ViewState, UiAction, OneShotEvents>(
    ViewState(FetchChartsDataUseCase())
//    ViewState(data = PieChartData(workoutsData = PieData(),
//                                bodyPartData = PieData(),
//                                exerciseBreakdown = PieData(),
//                                barViewData = BarViewData(BarData(),listOf()))
//    )
)
{
    init {
        Log.d("navhost","ChartsViewModel init")
    }
    override fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.OnResume -> runBlocking(Dispatchers.IO) {
               // val data = FetchChartsDataUseCase().data
                //_viewState.value = viewState.value.copy(data =  data)
                //val fetch = async { FetchViewPagerDataUseCase() }
            }
        }
    }
}

data class ViewState(
    val data : LiveData<FetchChartsDataUseCase.Results>
)

sealed class UiAction{
    object OnResume : UiAction()
//    object GoToExercisesList : UiAction()
//    object GoToMainViewPager : UiAction()
//    object GoToDiaryWithDay : UiAction()
//
//    class GoToAddExercises(val workoutExercise: WorkoutExercise): UiAction()

}
sealed class OneShotEvents{
//    class GoToAddExercise(val exerciseName: String): OneShotEvents()
//    class GoToExercisesList(val dateString: String): OneShotEvents()
//    class GoToDiary(val date: String): OneShotEvents()
//
//    object GoToMainViewPager : OneShotEvents()
}