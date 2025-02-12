package com.example.verifit.charts

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.verifit.*
import com.example.verifit.R
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.OnLifecycleEvent
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@ExperimentalComposeUiApi
class Compose_ChartActivity: AppCompatActivity() {
    // Helper Data Structure
    private val viewModel: ChartsViewModel by viewModels {
        ChartsViewModelFactory(
            WorkoutServiceSingleton.getWorkoutService(applicationContext),
            DateSelectStore,
            KnownExerciseServiceSingleton.getKnownExerciseService(applicationContext))
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                ChartsScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun ChartsScreenHilt(){
    val viewModel: ChartsViewModel = hiltViewModel()

    ChartsScreen(viewModel = viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun ChartsScreen(storeOwner: ViewModelStoreOwner){
    val context = LocalContext.current
    val viewModel: ChartsViewModel = viewModel (viewModelStoreOwner = storeOwner, factory =
    ChartsViewModelFactory(WorkoutServiceSingleton.getWorkoutService(context = context), DateSelectStore,KnownExerciseServiceSingleton.getKnownExerciseService(context = context))
    )

    ChartsScreen(viewModel = viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun ChartsScreen(viewModel: ChartsViewModel){

    val state = viewModel.viewState.collectAsState()
    val context = LocalContext.current
val scroll = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = {

                    Text(text = "Charts",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) // titl
                },
                actions = {

                }
            )
        },
        content = { padding ->
            Column(Modifier
                .background(colorResource(R.color.core_grey_05))
                .verticalScroll(scroll)
            ) {
                val pieState = state.value.data.observeAsState()
                pieState.value?.data?.let { pieData ->
                    CardChart("Workouts", "Number of workouts performed per year", content = {
                        TotalWorkoutsChart(pieData.workoutsData)
                    })


//                Card(Modifier.padding(15.dp)) {
//                    Column() {
//                        Text(text = "Workouts",
//                            modifier = Modifier.padding(start = 15.dp, top = 15.dp),
//                            fontSize = 26.sp)
//                        Text(text = "Number of workouts performed per year",
//                            modifier = Modifier
//                                .padding(start = 15.dp, top = 2.dp)
//                                .alpha(.6f), fontSize = MaterialTheme.typography.subtitle2.fontSize)
//                        Divider(color = MaterialTheme.colors.primary,
//                            thickness = 1.dp,
//                            modifier = Modifier.padding(top = 10.dp))
//
//
//                    }
//                }

                    CardChart("Bodypart Breakdown",
                        "See which bodyparts you focus on the most",
                        content = {
                            BodypartChart(pieData.bodyPartData)
                        })

                    CardChart("Exercise Breakdown",
                        "See which exercises you focus on the most",
                        content = {
                            BodypartChart(pieData.exerciseBreakdown)
                        })
                    CardChart("Daily Volume", "Total volume performed in each workout", content = {
                        BarGraph(pieData.barViewData)
                    })
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        },
        bottomBar = {
           // BottomNavigationComposable(BottomNavItem.Charts)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun CardChart(title: String,subtitle: String,content : @Composable () -> Unit = {}){
    Card(Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 0.dp)) {
        Column() {
            Text(text = title,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                fontSize = 26.sp)
            Text(text = subtitle,
                modifier = Modifier
                    .padding(start = 15.dp, top = 4.dp)
                    .alpha(.6f), fontSize = MaterialTheme.typography.subtitle2.fontSize)
            Divider(color = MaterialTheme.colors.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 6.dp))
            content()
            Spacer(modifier = Modifier.size(8.dp))
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun BodypartChart(data: PieData){
    AndroidView(factory = { context ->
        PieChart(context).apply {
            setUsePercentValues(false)
            getDescription().setEnabled(false)
            setExtraOffsets(5f, 10f, 5f, 5f)
            setDragDecelerationFrictionCoef(0.95f)
            setDrawHoleEnabled(false)
            setHoleColor(android.graphics.Color.WHITE)
            setTransparentCircleRadius(60f)

        }
    }, update = { view ->
        if(data.dataSetCount > 0){
            data.setValueTextSize(15f)
            //view.animateY(1000, Easing.EaseInOutCubic)
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
            view.setData(data)
            //[view.data = data
            view.invalidate()
            //view.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
        } else {
            //view.animateY(1000, Easing.EaseInOutCubic)
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
        }

    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(top = 10.dp)
    )
}

data class HighlightWrapper(val x: Float, val y : Float, val dataSetIndex: Int )
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun BarGraph(data: BarViewData){
    val highlight = remember{ mutableStateOf<HighlightWrapper?>( null)}
    AndroidView(factory = { context ->
        BarChart(context).apply {
            // Remove Legend
            val l: Legend = getLegend()
            l.isEnabled = false
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    h?.let {
                        highlight.value = HighlightWrapper(x = h.x,y= h.y, dataSetIndex = h.dataSetIndex)
                    }

                }

                override fun onNothingSelected() {
                    highlight.value = null
                }

            })
            highlight.value?.let {
                highlightValue(Highlight(it.x,it.y,it.dataSetIndex))
            }

        }
    }, update = { view ->
        if(data.barData.dataSetCount > 0){
            view.visibility = View.VISIBLE
            view.getXAxis().setValueFormatter(IndexAxisValueFormatter(data.workoutDates))
            view.setFitBars(true)
            view.setData(data.barData)
            view.getDescription().setText("")
            highlight.value?.let {
                view.highlightValue(Highlight(it.x,it.y,it.dataSetIndex))
            }

            view.invalidate()
            //view.animateY(500)
            view.setScaleMinima(1f, 1f)
        } else {
            view.visibility = View.INVISIBLE
        }

    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(top = 10.dp)
    )
}

class BarViewData(
    val barData: BarData,
    val workoutDates: List<String>
) {

}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun TotalWorkoutsChart(data: PieData){
    val animated = remember{ mutableStateOf(false)}
    AndroidView(factory = { context ->
        PieChart(context).apply {
            setUsePercentValues(false)
            getDescription().setEnabled(false)
            setExtraOffsets(5f, 10f, 5f, 5f)
            setDragDecelerationFrictionCoef(0.95f)
            setDrawHoleEnabled(false)
            setHoleColor(android.graphics.Color.WHITE)
            setTransparentCircleRadius(60f)


//            setUsePercentValues(false)
//            description.isEnabled = false
//            setExtraOffsets(5f,10f,5f,5f)
//            dragDecelerationFrictionCoef = 0.95f
//            isDrawHoleEnabled = false
//            setHoleColor(android.graphics.Color.WHITE)
//            transparentCircleRadius = 60f
            //animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
            //setNoDataText("No Workouts")
            //legend.isEnabled = false


        }
    }, update = { view ->
        if(data.dataSetCount > 0){
            data.setValueTextSize(15f)
//            if(!animated.value){
//               view.animateY(1000, Easing.EaseInOutCubic)
//                animated.value = true
//            }
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
            view.setData(data)
            //[view.data = data
            view.invalidate()
            //view.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
        } else {
//            if(!animated.value){
//                view.animateY(1000, Easing.EaseInOutCubic)
//                animated.value = true
//            }

            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
        }

    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(top = 10.dp)
    )
}



class ChartsViewModelFactory(
    val workoutService: WorkoutService,
    val dateSelectStore: DateSelectStore,
    val knownExerciseService: KnownExerciseService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChartsViewModel(
            FetchChartsDataUseCase = FetchChartsDataUseCaseImpl(workoutService)
            //FetchDaysWorkoutsUseCaseImpl(workoutService,dateSelectStore,knownExerciseService,colorGetter = ColorGetterImpl(knownExerciseService))
        )
                as T
    }
}

