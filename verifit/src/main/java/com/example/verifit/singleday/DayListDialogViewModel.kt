package com.example.verifit.singleday.dialog

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.verifit.addexercise.history.date
import com.example.verifit.common.NavigateToViewPagerUseCase
import com.example.verifit.common.NoOpNavigateToViewPagerUseCase
import com.example.verifit.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class DayListDialogViewModel @Inject constructor(
                                                 val NavigateToViewPagerUseCase: NavigateToViewPagerUseCase = NoOpNavigateToViewPagerUseCase(),
                                                 val savedStateHandle: SavedStateHandle?
                   )
        : BaseViewModel<ViewState, UiAction, OneShotEvents>(
            initialViewState = ViewState(

                    date = calcDateString(savedStateHandle?.date!!))
    ) {
    val date = savedStateHandle?.date!!
        override fun onAction(uiAction: UiAction) {
            when (uiAction) {
                UiAction.GoToMainViewPager -> {
                    Log.d("ViewPagerViewModel","day to Viewpager = $date")
                    NavigateToViewPagerUseCase(date)
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


}


data class ViewState(

        val date: String
)

sealed class UiAction{
    object GoToMainViewPager : UiAction()

}
sealed class OneShotEvents{

}