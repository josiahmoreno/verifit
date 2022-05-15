package com.example.verifit.calendar

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.verifit.DaggerVerifitApp_HiltComponents_SingletonC
import com.example.verifit.KnownExerciseService
import com.example.verifit.calendar.daycontent.DayContentViewModel
import com.example.verifit.calendar.daycontent.ViewState
import com.example.verifit.charts.ChartsViewModel
import com.example.verifit.charts.FetchChartsDataUseCaseImpl
import com.example.verifit.main.BaseViewModel
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.kotlinxDateTime.now
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import io.github.boguszpawlowski.composecalendar.week.DefaultWeekHeader
import kotlinx.datetime.*
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalenderScreenHilt(){
    CalenderScreen(hiltViewModel())
}

@Composable
fun CalenderScreen(viewModel: CalendarViewModel){
    val state = viewModel.viewState.collectAsState()
    val listState = rememberLazyListState(Int.MAX_VALUE/2)
    val currentMonth = Int.MAX_VALUE /2

     val onSelectionChanged: (List<java.time.LocalDate>) -> Boolean = { list: List<java.time.LocalDate> ->

         true
    }
    val selectionState: DynamicSelectionState = DynamicSelectionState(onSelectionChanged,
        //listOf(state.value.currentSelection),
        emptyList(),
        SelectionMode.Single)

    val now = java.time.LocalDate.now()
        LazyColumn(state = listState,content = {
            items(Int.MAX_VALUE, key = { message ->
                // Return a stable + unique key for the item
                message
            }){ index ->
                val monthOffset = index - currentMonth
                Log.d("Calendar", "currentMonth = $currentMonth, index = $index, monthOffset = $monthOffset")
                Calender(
                    dayContent = { kotlinDayState -> DayContent(dayState = kotlinDayState) },
                    date = now.minusMonths( monthOffset.toLong()),
                    selectionState = selectionState,
                    onSelectionChanged = onSelectionChanged,
                )

            }
    }

        )

}

@Composable
fun Calender(date: java.time.LocalDate,selectionState: DynamicSelectionState,
             onSelectionChanged: (List<java.time.LocalDate>) -> Boolean,
             dayContent: @Composable BoxScope.(KotlinDayState<DynamicSelectionState>) -> Unit,
            // fetcher: (java.time.LocalDate) -> List<Color>
){


    SelectableCalendar(
        calendarState = rememberSelectableCalendarState(
            confirmSelectionChange = { selection -> onSelectionChanged(selection) },
            initialSelectionMode = SelectionMode.Multiple,
            initialMonth = YearMonth.of(date.year,date.monthValue),
            selectionState = selectionState
        ),
        today = date,
        weekHeader = {
            DefaultWeekHeader2(it, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))
        },
        monthHeader = { monthState ->
            Box(modifier = Modifier.size(32.dp).background(Color.Green),contentAlignment = Alignment.Center, ){

            //Text(text = "${monthState.currentMonth.month} ${monthState.currentMonth.year}", fontWeight = FontWeight.Bold, color = LocalContentColor.current )
            }
        },
        showAdjacentMonths = false,
        horizontalSwipeEnabled = false,
        dayContent = { dayState ->
            dayContent(
                KotlinDayState(
                    date = dayState.date,
                    isCurrentDay = dayState.isCurrentDay,
                    selectionState = dayState.selectionState,

                )
            )
        }
    )
}

@Composable
public fun DefaultWeekHeader2(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = "",
                //dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight =  fontWeight,
                modifier = modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}


@Composable
fun BoxScope.DayContent(
    dayState: KotlinDayState<DynamicSelectionState>,

) {
    val viewModel : DayContentViewModel = hiltViewModel()
    val colorsState: State<List<Int>> = viewModel.fetchForDate(dayState.date).observeAsState(
        emptyList())
    val isSelected = dayState.selectionState.isDateSelected(dayState.date)

    Log.d("Calendar", "DayContent = recomp")
    Text(
        //text = dayState.date.dayOfMonth.toString(),
        text = "",
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .aspectRatio(1f)
            .clickable {
                dayState.selectionState.onDateSelected(dayState.date)
            },
        color = if (isSelected) Color.Red else  LocalContentColor.current,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body1,
    )

    Row(modifier = Modifier
        .align(Alignment.Center)
        .height(12.dp)
        .fillMaxWidth()
        //.background(Color.Black.copy(alpha = .2f))
        ,
        horizontalArrangement = Arrangement.Center
    ) {
        colorsState.value.map { Color(it) }.forEach { color: Color ->

            Canvas(modifier = Modifier.size(12.dp,12.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawCircle(
                    color = color,
                    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    radius = size.minDimension / 4
                )
            }
            //Text(text = "${color.colorSpace.name}")
        }
    }


}

data class KotlinDayState<T : SelectionState>(
    val date: java.time.LocalDate,
    val isCurrentDay: Boolean,
    val selectionState: T,
)

