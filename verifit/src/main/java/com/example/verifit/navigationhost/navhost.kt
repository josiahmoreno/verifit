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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.verifit.AddExerciseScreenHilt
import com.example.verifit.DaggerVerifitApp_HiltComponents_SingletonC
import com.example.verifit.HistoryContent
import com.example.verifit.HistoryContentHilt
import com.example.verifit.addexercise.composables.*
import com.example.verifit.addexercise.deleteset.DeleteSetContent
import com.example.verifit.charts.ChartsScreen
import com.example.verifit.customexercise.CustomExerciseScreen
import com.example.verifit.customexercise.CustomExerciseScreenHilt
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.DaggerScreen1Component
//import com.example.verifit.di.Screen1Component
//import com.example.verifit.di.Services.getSavedStateHandle
import com.example.verifit.diary.DiaryListScreen
import com.example.verifit.diary.day.DiaryDayContent
import com.example.verifit.diary.workoutexercisestats.WorkoutExerciseStatsContent
import com.example.verifit.exercises.ExercisesList
import com.example.verifit.exercises.ExercisesListHilt
import com.example.verifit.exercises.ExercisesListViewModel
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.ViewPagerScreen
import com.example.verifit.main.ViewPagerScreenHilt
import com.example.verifit.main.ViewPagerViewModel
import com.example.verifit.me.MeScreen
import com.example.verifit.singleday.DayListScreen
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.internal.DaggerGenerated
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseAppActivity : ComponentActivity() {
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun viewPagerViewModelFactory(): ViewPagerViewModel.Factory
        fun exercisesListViewModelFactory(): ExercisesListViewModel.Factory
        fun addExerciseViewModelFactory(): AddExerciseViewModel.Factory
    }



    @Inject
    lateinit var navController : NavHostController

//    @Inject
//    lateinit var graphDialog : @Composable () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                ExerciseApp(navController)
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ExerciseApp(navController: NavHostController) {

    val backstackEntry = navController.currentBackStackEntryAsState()
    val testStart = backstackEntry.value?.destination?.parent?.startDestinationRoute
    val parentRoute = backstackEntry.value?.destination?.parent?.route
    val testRoot = backstackEntry.value?.destination?.route
    Log.d("navhost", "testdestination =  ${testRoot} startDestinationRoute = ${testStart}, parentRoute ${parentRoute}")

    val currentScreen = BottomNavItem.fromRoute(parentRoute)




    Scaffold(bottomBar = {
        if (testRoot == testStart || currentScreen.title == testRoot) {
           // BottomNavigationComposable(currentItem = currentScreen,
             //   navHostController = navController)
        } else {
            Log.d("navhost", "non root, destination =  ${testRoot} parent start ${testStart}")
        }

    }) { paddingValues ->
        Log.d("navhost", "   padding ${paddingValues}")
        NavHost(navController = navController, Modifier.padding(paddingValues))
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class)
@Composable
fun NavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
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
            ) { backStackEntry ->
                Log.d("navHost.ViewPagerScreen", "nav hosting")
                ViewPagerScreenHilt(date = backStackEntry.arguments?.getString("date"))
            }
        }

        //val addRoute = BottomNavItem.Exercises.title
       // navigation(startDestination = "list?date={date}", route = addRoute) {
            composable("list?date={date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                }))
            { backStackEntry ->
                ExercisesListHilt(date = backStackEntry.arguments?.getString("date"))
            }
            composable(route = "add_exercise/{exercise_name}/{date}",
                arguments = listOf(navArgument("exercise_name") { type = NavType.StringType })
            ) { backStackEntry ->
                AddExerciseScreenHilt(exerciseName = backStackEntry.arguments?.getString("exercise_name"),
                    date = backStackEntry.arguments?.getString("date"))
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
            ) { backStackEntry ->


                //DaggerVerifitApp_HiltComponents_SingletonC.builder()
                //val com = DaggerScreen1Component.builder().build()
                //DaggerVerifitApp_HiltComponents_SingletonC..builder().build().
                GraphContentHilt()

                //GraphContent(exerciseName = backStackEntry.arguments?.getString("exercise_name"))
            }
            dialog(
                route = "timer",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
            ) { backStackEntry ->
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
//
            //                CommentContent(navHostController = navController,exerciseName = backStackEntry.arguments?.getString("exercise_name"),
//                    backStackEntry.arguments?.getString("date"), comment = backStackEntry.arguments?.getString("comment"))
            }
            dialog(route = "delete_set/{identifier}",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                arguments = listOf(
                    navArgument("identifier") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                DeleteSetContent(navHostController = navController,
                    identifier = backStackEntry.arguments?.getString("identifier")!!)
            }
       // }

        navigation(startDestination = "diary_list?date={date}", route = BottomNavItem.Diary.title) {
            composable("diary_list?date={date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                    nullable = true;
                    defaultValue = null
                }
                )) { backStackEntry ->
                DiaryListScreen(navigateTo = { name ->
                    navController.navigate("add_exercise/${name}"){

                    }
                }, navController = navController, backStackEntry.arguments?.getString("date"), root = "diary_list?date=${ backStackEntry.arguments?.getString("date")}")
            }
            dialog(route = "diary_day_stats/{date}",
                dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                DiaryDayContent(navHostController = navController,
                    date = backStackEntry.arguments?.getString("date"),
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
            ) { backStackEntry ->
                WorkoutExerciseStatsContent(navHostController = navController,
                    exerciseName = backStackEntry.arguments?.getString("exercise_name"),
                    date = backStackEntry.arguments?.getString("date")) { navController.popBackStack() }
            }
            composable("day_list/{date}",
                arguments = listOf(navArgument("date") {
                    type = NavType.StringType
                }
                )) { backStackEntry ->
                DayListScreen(navController = navController,
                    backStackEntry.arguments?.getString("date")!!)
            }

        }
        navigation(startDestination = "twocharts", route = BottomNavItem.Charts.title) {
            composable("twocharts") {
                ChartsScreen(viewModelStoreOwner)

            }
        }

        navigation(startDestination = "me_start", route = BottomNavItem.Me.title) {
            composable("me_start") {
                MeScreen()
            }
        }
    }

}
