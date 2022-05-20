package com.example.verifit.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.verifit.R
import com.example.verifit.charts.BarGraph
import com.example.verifit.charts.BodypartChart
import com.example.verifit.charts.CardChart
import com.example.verifit.charts.TotalWorkoutsChart
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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
    val categoryCircle1 = view.findViewById<View>(com.example.verifit.R.id.categoryCircle1)
    val categoryCircle2 = view.findViewById<View>(com.example.verifit.R.id.categoryCircle2)
    val categoryCircle3 = view.findViewById<View>(com.example.verifit.R.id.categoryCircle3)
    val categoryCircle4 = view.findViewById<View>(com.example.verifit.R.id.categoryCircle4)
    //val textView = view.findViewById<ComposeView>(com.example.verifit.R.id.my_composable)
    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}
class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(com.example.verifit.R.id.calendarMonthText)
    val legendLayout =  view.findViewById<LinearLayout>(com.example.verifit.R.id.legendLayout)

}
 fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)


fun View.addBackgroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@Composable
fun CalenderScreen3(viewModel: CalendarViewModel) {
    val state: State<ViewState> = viewModel.viewState.collectAsState()

    androidx.compose.material.Scaffold (
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = {

                    Text(text = "calendar",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) // titl
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onAction(UiAction.GoToToday)
                    }) {
                        Icon(Icons.Filled.Today, "comment")
                    }

                }
            )
        },
        content = { padding ->
            android(state,viewModel)
        },
        bottomBar = {
            // BottomNavigationComposable(BottomNavItem.Charts)
        }
    )



}

@Composable
fun android(state: State<ViewState>,viewModel: CalendarViewModel) {
    val dateState = remember{ mutableStateOf<LocalDate?>(null)}
    val effect = remember {
        mutableStateOf<YearMonth?>(null)
    }

    LaunchedEffect(key1 = "calendar", block = {

        viewModel.oneShotEvents
            .onEach {
                when (it) {
                    is OneShotEvents.ScrollToMonth -> effect.value = it.now
                }
            }.collect()
    })
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
                        //val key =  "${day.date.dayOfMonth}"+"${day.date.monthValue}" +"${day.date.year}"
                        val key = day.date.format(formatter)
                        val hashMapData = state.value.categoryData
                        if(hashMapData.containsKey(key)){
                            val dayViewData = hashMapData[key]
                            dayViewData?.categories?.forEachIndexed { index, categoryColor ->
                                val view : View? = when(index){
                                    0 ->container.categoryCircle1
                                    1 ->container.categoryCircle2
                                    2 ->container.categoryCircle3
                                    3 ->container.categoryCircle4
                                    else -> null
                                }
                                view?.let {
                                    it.visibility = View.VISIBLE
                                    ViewCompat.setBackgroundTintList(
                                        it,
                                        ColorStateList.valueOf(categoryColor));
                                }
                            }
                        } else {
                            container.categoryCircle1.visibility  = View.GONE
                            container.categoryCircle2.visibility = View.GONE
                            container.categoryCircle3.visibility = View.GONE
                            container.categoryCircle4.visibility = View.GONE
                        }
                    } else {
                        container.textView.setTextColor(android.graphics.Color.TRANSPARENT)
                        container.selectCircle.visibility = View.GONE
                        container.textView.setBackgroundResource(0)
                       container.categoryCircle1.visibility  = View.GONE
                        container.categoryCircle2.visibility = View.GONE
                        container.categoryCircle3.visibility = View.GONE
                        container.categoryCircle4.visibility = View.GONE
                    }
                    container.view.setOnClickListener {
                        // Check the day owner as we do not want to select in or out dates.
                        if (day.owner == DayOwner.THIS_MONTH) {
                            viewModel.onAction(UiAction.OnDateClick(day.date))
                        }
                    }




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
            val firstMonth = currentMonth.minusYears(5)
            val lastMonth = currentMonth.plusYears(5)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

    }, update = { view ->

        if(dateState.value != state.value.currentSelection){
            dateState.value?.let {
                Log.d("Calendar","update local date state")
                view.notifyDateChanged(it)
            }
            dateState.value = state.value.currentSelection
            state.value.currentSelection?.let { date: LocalDate ->
                Log.d("Calendar","current date selection")
                view.notifyDateChanged(date)
            }
        }

        effect.value?.let{
            Log.d("Calendar","scrollToMonth")
            view.scrollToMonth(it)
            effect.value = null
        }



    }, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 10.dp)
    )
}


