package com.example.verifit.calendar

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

@Composable
fun CalenderScreenHilt2() {
    CalenderScreen3(hiltViewModel())
}

@Composable
fun CalenderScreen2(viewModel: CalendarViewModel) {
    val state = viewModel.viewState.collectAsState()
    AndroidView( factory = { context ->
        CalendarView(context).apply {
            val calendar = Calendar.getInstance()

            // Initial date
            calendar.set(2018, Calendar.JUNE, 1)
            val initialDate = CalendarDate(calendar.time)

            // Minimum available date
            //calendar.set(2018, Calendar.MAY, 15)
            //val minDate = CalendarDate(calendar.time)

            // Maximum available date
            //calendar.set(2018, Calendar.JULY, 15)
            //val maxDate = CalendarDate(calendar.time)

            // List of preselected dates that will be initially selected
            val preselectedDates: List<CalendarDate> = listOf(CalendarDate(state.value.currentSelection!!.toEpochDay()))

            // The first day of week
            val firstDayOfWeek = java.util.Calendar.MONDAY
            setupCalendar(
                initialDate = initialDate,
                //minDate = minDate,
               // maxDate = maxDate,
                selectionMode = CalendarView.SelectionMode.SINGLE,
                selectedDates = preselectedDates,
                firstDayOfWeek = firstDayOfWeek,
                showYearSelectionView = false
            )

        }

    }, update = { view ->


    }, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 10.dp)
    )


}
fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}


class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(com.example.verifit.R.id.calendarDayText)
    val selectCircle = view.findViewById<View>(com.example.verifit.R.id.selectCircle)
    //val textView = view.findViewById<ComposeView>(com.example.verifit.R.id.my_composable)
    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}
class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(com.example.verifit.R.id.calendarMonthText)
    val legendLayout =  view.findViewById<LinearLayout>(com.example.verifit.R.id.legendLayout)

}
 fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun View.addBackgroundCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun View.addBackgroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}
@Composable
fun CalenderScreen3(viewModel: CalendarViewModel) {

    val state = viewModel.viewState.collectAsState()
    val dateState = remember{ mutableStateOf<LocalDate?>(null)}
    AndroidView( factory = { context ->
        com.kizitonwose.calendarview.CalendarView(context = context).apply {
            dayViewResource = com.example.verifit.R.layout.compose_day
            dayBinder = object : DayBinder<DayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = DayViewContainer(view)

                // Called every time we need to reuse a container.
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        container.textView.text =  day.date.dayOfMonth.toString()
                    } else {
                        //container.textView.text =  day.date.dayOfMonth.toString()+"aa"
                    }
                    if (day.owner == DayOwner.THIS_MONTH) {
                        container.textView.setTextColor(android.graphics.Color.GRAY)
                        if (day.date == state.value.currentSelection) {
                            // If this is the selected date, show a round background and change the text color.
                            container.textView.setTextColor(android.graphics.Color.WHITE)

                            container.selectCircle.visibility = VISIBLE
                        } else {
                            // If this is NOT the selected date, remove the background and reset the text color.
                            container.textView.setTextColor(android.graphics.Color.BLACK)
                            container.textView.addBackgroundRipple()
                            container.selectCircle.visibility = View.GONE
                        }
                    } else {
                        container.textView.setTextColor(android.graphics.Color.TRANSPARENT)
                        container.selectCircle.visibility = View.GONE
                    }
                    container.view.setOnClickListener {
                        // Check the day owner as we do not want to select in or out dates.
                        if (day.owner == DayOwner.THIS_MONTH) {
                            viewModel.onAction(UiAction.OnDateClick(day.date))
                        }
                    }
//                    if (day.owner == DayOwner.THIS_MONTH) {
//                        container.textView.setTextColor(android.graphics.Color.WHITE)
//                    }
//                    container.textView.setContent {
//                        Box(contentAlignment = Alignment.Center){
//                            Text(text = day.day.toString() )
//                        }
//                    }
                }
            }
            val daysOfWeek = daysOfWeekFromLocale()
            monthHeaderResource = com.example.verifit.R.layout.compose_month
            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = MonthViewContainer(view)

                // Called every time we need to reuse a container.
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val m = month.yearMonth.month.name.capitalize(Locale.getDefault())
                    val mm = "$m ${month.year}"
                    container.textView.text = mm
                        // Setup each header day text if we have not done that already.
                        if (container.legendLayout.tag == null) {
                            container.legendLayout.tag = month.yearMonth
                            container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                    .toUpperCase(Locale.ENGLISH)

                                tv.setTextColor(Color.GRAY)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            }
                            month.yearMonth
                        }

                }
            }
            hasBoundaries = true
            setMonthMargins(bottom = 30)
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(10)
            val lastMonth = currentMonth.plusMonths(10)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

    }, update = { view ->

        dateState.value?.let {
            view.notifyDateChanged(it)
        }
        dateState.value = state.value.currentSelection
       state.value.currentSelection?.let { date: LocalDate ->
           view.notifyDateChanged(date)
       }


    }, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 10.dp)
    )


}


