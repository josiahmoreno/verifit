package com.example.verifit.bottomnavigation

import android.content.Intent
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.verifit.MainActivity
import com.example.verifit.R
import com.example.verifit.charts.Compose_ChartActivity
import com.example.verifit.diary.Compose_DiaryActivity
import com.example.verifit.exercises.Compose_ExercisesActivity
import com.example.verifit.main.BottomNavItem
import com.example.verifit.main.Compose_MainActivity
import com.example.verifit.main.getActivity
import com.example.verifit.me.Compose_MeActivity
import com.example.verifit.singleton.DateSelectStore
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomNavigationComposable(currentItem: BottomNavItem) {
    val items : List<BottomNavItem> = listOf(
            BottomNavItem.Diary,
            //BottomNavItem.Exercises,
            BottomNavItem.Home,
            BottomNavItem.Charts,
        BottomNavItem.Me
    )
    val context = LocalContext.current
    androidx.compose.material.BottomNavigation(
            backgroundColor = colorResource(id = R.color.core_white),
            contentColor = Color.Black
    ) {
        //val navBackStackEntry by navController.currentBackStackEntryAsState()
        //val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item : BottomNavItem ->
            BottomNavigationItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = {
                        Text(text = item.title,
                                fontSize = 9.sp)
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = Color.Black.copy(0.6f),
                    alwaysShowLabel = true,
                    selected = item == currentItem,
                    onClick = {
                        if(currentItem == item){
                            return@BottomNavigationItem
                        }
                        when (item) {
                            BottomNavItem.Diary -> {
                                val intent = Intent(context, Compose_DiaryActivity::class.java)
                                // Date selected is by default today
                                intent.putExtra("date", DateSelectStore.date_selected)
                                context.startActivity(intent)
                                context.getActivity()?.overridePendingTransition(0, 0)

                            }
//                            is BottomNavItem.Exercises -> {
//
//                                val intent = Intent(context, Compose_ExercisesActivity::class.java)
//                                // Date selected is by default today
//
//
//                                // Date selected is by default today
//                                val date_clicked = Date()
//                                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
//                                DateSelectStore.date_selected = dateFormat.format(date_clicked)
//                                context.startActivity(intent)
//                                context.getActivity()?.overridePendingTransition(0, 0)
//                            }
                            is BottomNavItem.Home -> {
                                val intent = Intent(context, Compose_MainActivity::class.java)
                                context.startActivity(intent)
                                context.getActivity()?.overridePendingTransition(0, 0)

                            }
                            BottomNavItem.Charts -> {
                                val intent = Intent(context, Compose_ChartActivity::class.java)
                                context.startActivity(intent)
                                context.getActivity()?.overridePendingTransition(0, 0)

                            }
                            BottomNavItem.Me -> {
                                val intent = Intent(context, Compose_MeActivity::class.java)
                                context.startActivity(intent)
                                context.getActivity()?.overridePendingTransition(0, 0)

                            }
                        }
                    }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomNavigationComposable(currentItem: BottomNavItem, navHostController: NavHostController) {
    val items = listOf(
        BottomNavItem.Diary,
        //BottomNavItem.Exercises,
        BottomNavItem.Home,
        BottomNavItem.Charts,
        BottomNavItem.Me
    )
    val context = LocalContext.current
    androidx.compose.material.BottomNavigation(
        backgroundColor = colorResource(id = R.color.core_white),
        contentColor = Color.Black
    ) {
        //val navBackStackEntry by navController.currentBackStackEntryAsState()
        //val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(text = item.title,
                        fontSize = 9.sp)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Black.copy(0.6f),
                alwaysShowLabel = true,
                selected = item == currentItem,
                onClick = {
                    navHostController.navigate(item.title) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}