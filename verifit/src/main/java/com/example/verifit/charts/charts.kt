package com.example.verifit.charts

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.verifit.*
import com.example.verifit.bottomnavigation.BottomNavigationComposable
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.OnLifecycleEvent
import com.example.verifit.singleton.DateSelectStore
import com.example.verifit.workoutservice.WorkoutService
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
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
fun ChartsScreen(viewModel: ChartsViewModel){

    val state = viewModel.viewState.collectAsState()
    val context = LocalContext.current
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            -> {
                viewModel.onAction(UiAction.OnResume)
            }
        }
    }

    LaunchedEffect(key1 = "ViewPagerScreen", block = {

        viewModel.oneShotEvents
            .onEach {
                when (it) {

                }
            }
            .collect()
    })
    //MaterialTheme() {

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
        content = {
            Column(Modifier.background(Color.LightGray)) {


                Card(Modifier.padding(15.dp)) {
                    Column() {
                        Text(text = "Workouts",
                            modifier = Modifier.padding(start = 15.dp, top = 8.dp),
                            fontSize = 26.sp)
                        Text(text = "Number of workouts performed per year",
                            modifier = Modifier
                                .padding(start = 15.dp, top = 2.dp)
                                .alpha(.6f), fontSize = MaterialTheme.typography.subtitle2.fontSize)
                        Divider(color = MaterialTheme.colors.primary,
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 10.dp))
                        TotalWorkoutsChart(state.value.data)

                    }
                }
                CardChart("Bodypart Breakdown","See which bodyparts you focus on the most",content = {
                    BodypartChart(state.value.data)
                })
            }
        },
        bottomBar = {
            BottomNavigationComposable(BottomNavItem.Charts)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun CardChart(title: String,subtitle: String,content : @Composable () -> Unit = {}){
    Card(Modifier.padding(15.dp)) {
        Column() {
            Text(text = title,
                modifier = Modifier.padding(start = 15.dp, top = 8.dp),
                fontSize = 26.sp)
            Text(text = subtitle,
                modifier = Modifier
                    .padding(start = 15.dp, top = 2.dp)
                    .alpha(.6f), fontSize = MaterialTheme.typography.subtitle2.fontSize)
            Divider(color = MaterialTheme.colors.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 10.dp))
            content()
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
            view.animateY(1000, Easing.EaseInOutCubic)
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
            view.setData(data)
            //[view.data = data
            //view.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
        } else {
            view.animateY(1000, Easing.EaseInOutCubic)
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
        }

    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(top = 10.dp)
    )
}


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun TotalWorkoutsChart(data: PieData){
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
            view.animateY(1000, Easing.EaseInOutCubic)
            view.setNoDataText("No Workouts")
            view.getLegend().setEnabled(false)
            view.setData(data)
            //[view.data = data
            //view.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
        } else {
            view.animateY(1000, Easing.EaseInOutCubic)
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

