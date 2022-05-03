package com.example.verifit.navigationhost

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.verifit.AddExerciseScreen
import com.example.verifit.HistoryContent
import com.example.verifit.addexercise.composables.CommentContent
import com.example.verifit.addexercise.composables.GraphContent
import com.example.verifit.addexercise.composables.TimerContent
import com.example.verifit.bottomnavigation.BottomNavigationComposable
import com.example.verifit.charts.ChartsScreen
import com.example.verifit.customexercise.CustomExerciseScreen
import com.example.verifit.diary.DiaryListScreen
import com.example.verifit.exercises.ExercisesList
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.ViewPagerScreen
import com.example.verifit.me.MeScreen
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi



    class ExerciseAppActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                AppCompatTheme{
                    ExerciseApp()
                }
            }
        }
    }
    @OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun ExerciseApp() {
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        val testStart = backstackEntry.value?.destination?.parent?.startDestinationRoute
        val parentRoute = backstackEntry.value?.destination?.parent?.route
        val testRoot = backstackEntry.value?.destination?.route
        Log.d("navhost","test ${testRoot} ${testStart}, parentRoute ${parentRoute}")

        val currentScreen = BottomNavItem.fromRoute(parentRoute)




        Scaffold(bottomBar ={
            if( testRoot == testStart || currentScreen.title == testRoot){
                BottomNavigationComposable(currentItem = currentScreen , navHostController = navController )
            } else {
                Log.d("navhost","non root ${testRoot} ${testStart}")
            }

        }) { paddingValues ->
            Log.d("navhost","   padding ${paddingValues}")
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
            composable(BottomNavItem.Home.title) {
                ViewPagerScreen(goToAddExercise = { name: String ->
                    navController.navigate("add_exercise/${name}")
                })
            }
            val addRoute = BottomNavItem.Exercises.title
            navigation(startDestination = "list", route = addRoute) {
                composable("list") {
                    ExercisesList(goToAddExercises = { name ->
                        navController.navigate("add_exercise/${name}")
                    },goToNewCustomExercise =  {

                    },navController)
                }
                composable(route = "add_exercise/{exercise_name}",
                    arguments = listOf(navArgument("exercise_name"){type = NavType.StringType})
                ) { backStackEntry ->
                    AddExerciseScreen(exerciseName = backStackEntry.arguments?.getString("exercise_name"),navController)
                }
                composable("new_exercise") { backStackEntry ->
                    CustomExerciseScreen(navController)
                }
                dialog(route = "history_dialog/{exercise_name}",
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                    arguments = listOf(navArgument("exercise_name"){type = NavType.StringType}, )
                ) { backStackEntry ->
                    HistoryContent(exerciseName = backStackEntry.arguments?.getString("exercise_name"))
                }
                dialog(route = "graph/{exercise_name}",
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                    arguments = listOf(navArgument("exercise_name"){type = NavType.StringType}, )
                ) { backStackEntry ->
                    GraphContent(exerciseName = backStackEntry.arguments?.getString("exercise_name"))
                }
                dialog(route = "timer",
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                ) { backStackEntry ->
                    TimerContent()
                }
                dialog(route = "comment/{exercise_name}",
                    dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                    arguments = listOf(navArgument("exercise_name"){type = NavType.StringType}, )
                ) { backStackEntry ->
                    CommentContent(exerciseName = backStackEntry.arguments?.getString("exercise_name"))
                }
            }

            navigation(startDestination = "diary_list", route = BottomNavItem.Diary.title) {
                composable("diary_list") {
                    DiaryListScreen(navigateTo = { name ->
                        navController.navigate("add_exercise/${name}"){
                            popUpTo("list") {

                            }
                        }
                    })
                }
            }
            navigation(startDestination = "twocharts", route = BottomNavItem.Charts.title) {
                composable("twocharts") {
                    ChartsScreen(viewModelStoreOwner)

                }
            }
            /*
            composable(BottomNavItem.Charts.title) {
                ChartsScreen(viewModelStoreOwner)

            }
             */
            navigation(startDestination = "me_start", route = BottomNavItem.Me.title) {
                composable("me_start") {
                    MeScreen()
                }
            }
        }
    }
