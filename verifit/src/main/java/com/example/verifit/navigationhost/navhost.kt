package com.example.verifit.navigationhost

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.verifit.AddExerciseScreenHilt
import com.example.verifit.HistoryContent
import com.example.verifit.HistoryContentHilt
import com.example.verifit.addexercise.composables.*
import com.example.verifit.addexercise.deleteset.DeleteSetContent
import com.example.verifit.addexercise.deleteset.DeleteSetContentHilt
import com.example.verifit.calendar.CalenderScreenHilt
import com.example.verifit.calendar.CalenderScreenHilt2
import com.example.verifit.charts.ChartsScreen
import com.example.verifit.charts.ChartsScreenHilt
import com.example.verifit.customexercise.CustomExerciseScreen
import com.example.verifit.customexercise.CustomExerciseScreenHilt
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.Screen1Component
//import com.example.verifit.di.Services.getSavedStateHandle
import com.example.verifit.diary.DiaryListScreen
import com.example.verifit.diary.DiaryListScreenHilt
import com.example.verifit.diary.day.DiaryDayContentHilt
import com.example.verifit.diary.workoutexercisestats.WorkoutExerciseStatsContentHilt
import com.example.verifit.exercises.ExercisesList
import com.example.verifit.exercises.ExercisesListHilt
import com.example.verifit.exercises.ExercisesListViewModel
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.ViewPagerScreen
import com.example.verifit.main.ViewPagerScreenHilt
import com.example.verifit.main.ViewPagerViewModel
import com.example.verifit.me.MeScreen
import com.example.verifit.settings.SettingsScreenHilt
import com.example.verifit.singleday.DayListDialogHilt
import com.example.verifit.singleday.DayListScreenHilt
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.internal.DaggerGenerated
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseAppActivity : ComponentActivity() {

    @Inject
    lateinit var auroraNavigator: AuroraNavigator

   init {
       Log.d("ExerciseAppActivity", "init")
   }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ExerciseApp", "init0")
        return androidx.tracing.trace("NavHost") {
            super.onCreate(savedInstanceState)
            setContent {
                Log.d("ExerciseApp", "init1")
                var navController: NavHostController = rememberNavController()
                AppCompatTheme {
                    LaunchedEffect(navController) {
                        auroraNavigator.destinations.collect {
                            when (val event = it) {
                                is NavigatorEvent.NavigateUp -> {
                                    navController.navigateUp()
                                }
                                is NavigatorEvent.Directions -> navController.navigate(
                                    event.destination,
                                    event.builder
                                )
                                NavigatorEvent.PopBackStack -> {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                    Log.d("ExerciseApp", "init2")
                    ExerciseApp(navController)
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ExerciseApp(navController: NavHostController) {
    Log.d("ExerciseApp", "init3")
//    val backstackEntry = navController.currentBackStackEntryAsState()
//    val testStart = backstackEntry.value?.destination?.parent?.startDestinationRoute
//    val parentRoute = backstackEntry.value?.destination?.parent?.route
//    val testRoot = backstackEntry.value?.destination?.route
//    Log.d("navhost",
//        "testdestination =  ${testRoot} startDestinationRoute = ${testStart}, parentRoute ${parentRoute}")

 //   val currentScreen = BottomNavItem.fromRoute(parentRoute)
    Scaffold(bottomBar = {
//        if (testRoot == testStart || currentScreen.title == testRoot) {
//            // BottomNavigationComposable(currentItem = currentScreen,
//            //   navHostController = navController)
//        } else {
//            Log.d("navhost", "non root, destination =  ${testRoot} parent start ${testStart}")
//        }

    }) { paddingValues ->
        Log.d("navhost", "   padding ${paddingValues}")
        NavHost(navController = navController, Modifier.padding(paddingValues))
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class)
@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.title,
        modifier = modifier
    ) {
        navigation(startDestination = "view_pager?date={date}", route = BottomNavItem.Home.title) {
            composable("view_pager?date={date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                    nullable = true;
                    defaultValue = null
                })
            ) {
                Log.d("navHost.ViewPagerScreen", "nav hosting")
                ViewPagerScreenHilt()
            }
        }

        //val addRoute = BottomNavItem.Exercises.title
        // navigation(startDestination = "list?date={date}", route = addRoute) {
        composable("list?date={date}",
            arguments = listOf(navArgument("date") {
                type = NavType.StringType
            }))
        {
            ExercisesListHilt()
        }
        composable(route = "add_exercise/{exercise_name}/{date}",
            arguments = listOf(navArgument("exercise_name") { type = NavType.StringType })
        ) {
            AddExerciseScreenHilt()
        }
        composable("new_exercise") {
            CustomExerciseScreenHilt()
        }
        dialog(route = "history_dialog/{exercise_name}",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            arguments = listOf(navArgument("exercise_name") { type = NavType.StringType })
        ) {
            HistoryContentHilt()

        }
        dialog(route = "graph/{exercise_name}",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            arguments = listOf(navArgument("exercise_name") { type = NavType.StringType })
        ) {
            GraphContentHilt()
        }
        dialog(
            route = "timer",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            TimerContentHilt()
        }
        dialog(route = "comment/{exercise_name}/{date}?comment={comment}",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            arguments = listOf(
                navArgument("exercise_name") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("comment") {
                    type = NavType.StringType
                    nullable = true;
                    defaultValue = null
                }
            )
        ) {
            CommentContentHilt()
        }
        dialog(route = "delete_set/{identifier}",
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            arguments = listOf(
                navArgument("identifier") { type = NavType.StringType }
            )
        ) {
            DeleteSetContentHilt()
        }
        // }

        navigation(startDestination = "diary_list?date={date}", route = BottomNavItem.Diary.title) {
            composable("diary_list?date={date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                    nullable = true;
                    defaultValue = null
                }
                )) { DiaryListScreenHilt() }
            dialog(route = "diary_day_stats/{date}",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) {
                DiaryDayContentHilt(
                    closeClick = {
                        navController.popBackStack()
                    })
            }
            dialog(route = "workout_exercise_stats/{exercise_name}/{date}",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                arguments = listOf(
                    navArgument("exercise_name") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                )
            ) {
                WorkoutExerciseStatsContentHilt() { navController.popBackStack() }
            }
            composable("day_list/{date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                }
                )) {
                DayListScreenHilt()
            }
            dialog("day_list_dialog/{date}",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                }
                )) {
                DayListDialogHilt()
            }

        }
        navigation(startDestination = "twocharts", route = BottomNavItem.Charts.title) {
            composable("twocharts") {
                ChartsScreenHilt()

            }
        }

        navigation(startDestination = "me _start", route = BottomNavItem.Me.title) {
            composable("me_start") {
                MeScreen()
            }
        }
        navigation(startDestination = "calendar/{date}", route = "calendar_route") {
            composable("calendar/{date}",arguments = listOf(navArgument("date") {
                type = NavType.StringType
            }
                    )) {
                CalenderScreenHilt2()

            }
        }

        composable("settings"){
            SettingsScreenHilt()
        }
    }

}
